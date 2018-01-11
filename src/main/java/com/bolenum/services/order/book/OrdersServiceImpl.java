package com.bolenum.services.order.book;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.bolenum.constant.UrlConstant;
import com.bolenum.enums.CurrencyType;
import com.bolenum.enums.OrderStandard;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.services.admin.fees.TradingFeeService;
import com.bolenum.services.user.transactions.TransactionService;
import com.bolenum.services.user.wallet.WalletService;
import com.bolenum.util.GenericUtils;

/**
 * 
 * @author Vishal Kumar
 * @date 06-Oct-2017
 * @modified Chandan Kumar Singh
 */

@Service
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private OrderAsyncService orderAsyncServices;

	@Autowired
	private TradingFeeService tradingFeeService;

	@Autowired
	private WalletService walletService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	public static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);

	List<Orders> ordersList = new ArrayList<>();

	List<Orders> matchedOrdersList = new ArrayList<>();

	/**
	 * this will check user wallet balance to get place an order
	 */
	@Override
	public String checkOrderEligibility(User user, Orders orders) {
		String tickter = null;
		String minOrderVol = null;
		String currencyType = null;
		/**
		 * to get the placed volume of selling currency
		 */
		Currency currency;
		/**
		 * if order type is SELL then only checking, user have selling volume
		 */
		if (OrderType.SELL.equals(orders.getOrderType())) {
			currency = orders.getPairedCurrency();
			tickter = currency.getCurrencyAbbreviation();
			currencyType = currency.getCurrencyType().toString();
			minOrderVol = String.valueOf(orders.getVolume());
			logger.debug("user: {} should have: {} {}", user.getEmailId(), minOrderVol, tickter);
		} else {
			minOrderVol = walletService.getPairedBalance(orders, orders.getMarketCurrency(), orders.getPairedCurrency(),
					orders.getVolume());
			currency = orders.getMarketCurrency();
			tickter = currency.getCurrencyAbbreviation();
			currencyType = currency.getCurrencyType().toString();
			logger.debug("user: {} should have: {} {}", user.getEmailId(), minOrderVol, tickter);
		}
		double userPlacedOrderVolume = getPlacedOrderVolumeOfCurrency(user, OrderStatus.SUBMITTED, OrderType.SELL,
				currency);
		double userPlacedLockedOrderVolume = getLockedOrderVolumeOfCurrency(user, OrderStatus.COMPLETED, currency);

		logger.debug("user:{} placed order volume: {}, locked volume: {}, and order volume: {}", user.getEmailId(),
				GenericUtils.getDecimalFormatString(userPlacedOrderVolume),
				GenericUtils.getDecimalFormatString(userPlacedLockedOrderVolume),
				GenericUtils.getDecimalFormatString(Double.valueOf(minOrderVol)));
		double minBalance = Double.valueOf(minOrderVol) + userPlacedOrderVolume + userPlacedLockedOrderVolume;
		logger.debug("user: {}, minimum order volume required to buy/sell: {}", user.getEmailId(),
				GenericUtils.getDecimalFormatString(minBalance));
		// getting the user current wallet balance
		String balance = walletService.getBalance(tickter, currencyType, user);
		// user must have balance then user is eligible for placing order
		if (Double.valueOf(balance) > 0 && (Double.valueOf(balance) >= Double.valueOf(minBalance))) {
			balance = "proceed";
		}
		return balance;
	}

	/**
	 * @description getPlacedOrderVolumeOfCurrency @param @return double @exception
	 * 
	 */
	public double getPlacedOrderVolumeOfCurrency(User user, OrderStatus orderStatus, OrderType orderType,
			Currency pairedCurrency) {
		List<Orders> orders = ordersRepository.findByUserAndOrderStatusAndOrderTypeAndPairedCurrency(user, orderStatus,
				orderType, pairedCurrency);
		double total = 0.0;
		for (Orders order : orders) {
			total = total + order.getVolume() + order.getLockedVolume();
		}
		return total;
	}

	public double getLockedOrderVolumeOfCurrency(User user, OrderStatus orderStatus, Currency pairedCurrency) {
		List<Orders> orders = ordersRepository.findByUserAndOrderStatusAndPairedCurrency(user, orderStatus,
				pairedCurrency);
		double total = 0.0;
		for (Orders order : orders) {
			total = total + order.getVolume() + order.getLockedVolume();
		}
		return total;
	}

	/**
	 * 
	 * @description get user order placed volume
	 * @param user
	 * @return balance
	 */
	@Override
	public double getPlacedOrderVolume(User user) {
		List<Orders> orders = findOrdersListByUserAndOrderStatus(user, OrderStatus.SUBMITTED);
		double total = 0.0;
		for (Orders order : orders) {
			total = total + order.getVolume() + order.getLockedVolume();
		}
		return total;
	}

	@Override
	public Boolean processOrder(Orders orders) throws InterruptedException, ExecutionException {
		orders = ordersRepository.save(orders);
		logger.debug("saved requested order id: {}", orders.getId());
		Boolean status;
		if (OrderStandard.MARKET.equals(orders.getOrderStandard())) {
			logger.debug("Processing market order");
			status = processMarketOrder(orders);
		} else {
			logger.debug("Processing limit order");
			status = processLimitOrder(orders);
		}
		simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_ORDER,
				com.bolenum.enums.MessageType.ORDER_BOOK_NOTIFICATION);
		return status;
	}

	@Override
	public Orders deleteOrder(Long ordersId) {
		Orders orders = ordersRepository.findOne(ordersId);
		orders.setDeletedOn(new Date());
		orders.setOrderStatus(OrderStatus.CANCELLED);
		return ordersRepository.save(orders);
	}

	/**
	 * 
	 * @description to check user requested order and existing order
	 * @param requested
	 *            order, list of existing orders
	 * @return #true if user requested order is matched with own existing user else
	 *         #false
	 */
	@Override
	public boolean isUsersSelfOrder(Orders reqOrder, List<Orders> orderList) {
		if (!orderList.isEmpty()) {
			Orders matchedOrder = matchedOrder(orderList);
			long matchedUserId = matchedOrder.getUser().getUserId();
			long reqOrderUserId = reqOrder.getUser().getUserId();
			logger.debug("matched user id: {} and reqested order user id: {}", matchedUserId, reqOrderUserId);
			if (matchedUserId == reqOrderUserId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * process market order
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Override
	public Boolean processMarketOrder(Orders orders) throws InterruptedException, ExecutionException {
		Boolean processed = false;
		OrderType orderType = orders.getOrderType();
		Currency marketCurrency = orders.getMarketCurrency();
		Currency pairedCurrency = orders.getPairedCurrency();
		logger.debug("Process Market Order, Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		logger.debug("Process Market Order, remaining Volume: {}", GenericUtils.getDecimalFormat(remainingVolume));
		if (OrderType.BUY.equals(orderType)) {
			List<Orders> sellOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndMarketCurrencyAndPairedCurrencyOrderByPriceAsc(OrderType.SELL,
							OrderStatus.SUBMITTED, marketCurrency, pairedCurrency);
			/**
			 * checking user self order, return false if self order else proceed. Feature
			 * has been paused on Dec 12 2017
			 */

			/*
			 * if (isUsersSelfOrder(orders, sellOrderList)) { return processed; }
			 */
			while (!sellOrderList.isEmpty() && remainingVolume > 0) {
				logger.debug("inner buy while loop for buyers remaining Volume: {}",
						GenericUtils.getDecimalFormat(remainingVolume));
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders, marketCurrency,
						pairedCurrency);
			}
			if (remainingVolume >= 0) {
				orders.setVolume(remainingVolume);
				/**
				 * if all volume traded then change status to completed of order
				 */
				if (remainingVolume == 0) {
					orders.setOrderStatus(OrderStatus.COMPLETED);
				}
				ordersList.add(orders);
				logger.debug("qty remaining so added in order book: {}", remainingVolume);
			}
			processed = true;
		} else {
			List<Orders> buyOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndMarketCurrencyAndPairedCurrencyOrderByPriceDesc(OrderType.BUY,
							OrderStatus.SUBMITTED, marketCurrency, pairedCurrency);
			/**
			 * checking user self order, return false if self order else proceed. checking
			 * user self order, return false if self order else proceed. Feature has been
			 * paused on Dec 12 2017
			 */
			/*
			 * if (isUsersSelfOrder(orders, buyOrderList)) { return processed; }
			 */
			logger.debug("buyOrderList.size(): {}", buyOrderList.size());
			while (!buyOrderList.isEmpty() && remainingVolume > 0) {
				logger.debug("inner sell while loop for sellers remaining Volume: {}",
						GenericUtils.getDecimalFormat(remainingVolume));
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders, marketCurrency,
						pairedCurrency);
			}
			if (remainingVolume >= 0) {
				orders.setVolume(remainingVolume);
				/**
				 * if all volume traded then change status to completed of order
				 */
				if (remainingVolume == 0) {
					orders.setOrderStatus(OrderStatus.COMPLETED);
				}
				ordersList.add(orders);
				logger.debug("remaining qty: {} added in book", remainingVolume);
			}
			processed = true;
		}
		logger.debug("MarketOrder: Order list saving started");
		/**
		 * if any exception occurs then clear list, otherwise double order will be
		 * placed
		 */
		try {
			orderAsyncServices.saveOrder(ordersList);
		} catch (Exception e) {
			logger.error("saving Process Market Order list: ", e.getMessage());
			ordersList.clear();
		}
		ordersList.clear();
		logger.debug("MarketOrder: Order list saving completed");
		return processed;
	}

	/**
	 * process limit order
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Override
	public Boolean processLimitOrder(Orders orders) throws InterruptedException, ExecutionException {
		Boolean processed = false;
		OrderType orderType = orders.getOrderType();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		Double price = orders.getPrice();
		Currency marketCurrency = orders.getMarketCurrency();
		Currency pairedCurrency = orders.getPairedCurrency();
		logger.debug("Order type is equal with buy: {}", orderType.equals(OrderType.BUY));
		// checking the order type is BUY
		if (OrderType.BUY.equals(orderType)) {
			// fetching the seller list whose selling price is less than equal
			// to buying price
			List<Orders> sellOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndMarketCurrencyAndPairedCurrencyAndPriceLessThanEqualOrderByPriceAsc(
							OrderType.SELL, OrderStatus.SUBMITTED, marketCurrency, pairedCurrency, price);
			/**
			 * checking user self order, return false if self order else proceed. Feature
			 * has been paused on Dec 12 2017
			 */

			/*
			 * if (isUsersSelfOrder(orders, sellOrderList)) { return processed; }
			 */
			/**
			 * fetch one best seller's price from list of sellers, order by price in ASC
			 * then process the order
			 */
			while (!sellOrderList.isEmpty() && (remainingVolume > 0) && (price >= getBestBuy(sellOrderList))) {
				logger.debug("inner buy while loop for buyers and remaining volume: {}", remainingVolume);
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders, marketCurrency,
						pairedCurrency);
			}
			if (remainingVolume >= 0) {
				orders.setVolume(remainingVolume);
				/**
				 * if all volume traded then change status to completed of order
				 */
				if (remainingVolume == 0) {
					orders.setOrderStatus(OrderStatus.COMPLETED);
				}
				ordersList.add(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		} else {
			/**
			 * fetching the list of BUYERS whose buy price is greater than sell price
			 */
			List<Orders> buyOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndAndMarketCurrencyAndPairedCurrencyAndPriceGreaterThanEqualOrderByPriceDesc(
							OrderType.BUY, OrderStatus.SUBMITTED, marketCurrency, pairedCurrency, price);
			/**
			 * checking user self order, return false if self order else proceed. Feature
			 * has been paused on Dec 12 2017
			 */
			/*
			 * if (isUsersSelfOrder(orders, buyOrderList)) { return processed; }
			 */

			/**
			 * fetch one best buyer's price from list of buyers, order by price in desc then
			 * process the order
			 */
			while (!buyOrderList.isEmpty() && (remainingVolume > 0) && (price <= buyOrderList.get(0).getPrice())) {
				logger.debug("inner sell while loop for seller and remaining volume: {}", remainingVolume);
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders, marketCurrency,
						pairedCurrency);
			}
			if (remainingVolume >= 0) {
				orders.setVolume(remainingVolume);
				/**
				 * if all volume traded then change status to completed of order
				 */
				if (remainingVolume == 0) {
					orders.setOrderStatus(OrderStatus.COMPLETED);
				}
				ordersList.add(orders);

				if (OrderType.SELL.equals(orderType)) {
					orderAsyncServices.saveLastPrice(marketCurrency, pairedCurrency, price);
				}
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		}
		logger.debug("Limit Order: order list saving started");
		try {
			orderAsyncServices.saveOrder(ordersList);
		} catch (Exception e) {
			ordersList.clear();
		}
		ordersList.clear();
		logger.debug("Limit Order: order list saving finished");
		return processed;
	}

	/**
	 * process the buyers and sellers order
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * 
	 */
	@Override
	public Double processOrderList(List<Orders> ordersList, Double remainingVolume, Orders orders,
			Currency marketCurrency, Currency pairedCurrency) throws InterruptedException, ExecutionException {
		// fetching order type BUY or SELL
		OrderType orderType = orders.getOrderType();
		User buyer;
		User seller;
		double buyerTradeFee;
		double sellerTradeFee;
		String toCA = pairedCurrency.getCurrencyAbbreviation();
		String pairCA = marketCurrency.getCurrencyAbbreviation();
		logger.debug("process order list remainingVolume: {}", remainingVolume);
		// process till order size and remaining volume is > 0
		while ((!ordersList.isEmpty()) && (remainingVolume > 0)) {
			logger.debug("inner proccessing while");
			Double qtyTraded; // total number quantity which is processed
			// fetch matched order object
			Orders matchedOrder = matchedOrder(ordersList);
			// checking selling/buying volume less than matched order volume
			logger.debug("matched order volume: {}", matchedOrder.getVolume());
			if (remainingVolume < matchedOrder.getVolume()) {
				// qtyTraded is total selling/buying volume
				qtyTraded = remainingVolume;
				logger.debug("qty traded: {}", qtyTraded);
				// setting new required SELL/BUY volume is remaining order
				// volume
				double remain = GenericUtils.getDecimalFormat(matchedOrder.getVolume() - remainingVolume);
				logger.debug("reamining volume: {}", remain);
				matchedOrder.setVolume(remain);
				logger.debug("reamining volume after set: {}", matchedOrder.getVolume());
				// adding matched order in list with remaining volume
				ordersList.add(matchedOrder);
				// now selling/buying volume is 0
				remainingVolume = 0.0;
			} else {
				// selling/buying volume greater than matched order volume
				// qtyTraded is total sellers/buyers volume
				qtyTraded = matchedOrder.getVolume();
				logger.debug("qty traded else: {}", qtyTraded);
				// new selling/buying volume is remainingVolume - qtyTraded
				remainingVolume = GenericUtils.getDecimalFormat(remainingVolume - qtyTraded);
				logger.debug("remaining volume else: {}", remainingVolume);
				// removed processed order
				removeOrderFromList(ordersList);
				// new volume of processed order is 0
				matchedOrder.setVolume(0.0);
				// status of processed order is completed
				matchedOrder.setOrderStatus(OrderStatus.COMPLETED);
				// adding to order list by setting the new volume and status of
				// processed order
				logger.debug("matching buy/sell completed");
			}
			buyerTradeFee = tradingFeeService.calculateFee(qtyTraded * matchedOrder.getPrice());
			sellerTradeFee = buyerTradeFee;
			logger.info("buyer trade fee: {} seller trade fee: {}", GenericUtils.getDecimalFormatString(buyerTradeFee),
					GenericUtils.getDecimalFormatString(sellerTradeFee));
			// checking the order type BUY
			if (OrderType.BUY.equals(orderType)) {
				// buyer is coming order's user
				buyer = orders.getUser();
				// seller is matched order's user
				seller = matchedOrder.getUser();
				/**
				 * Setting the locked volume of orders, if trade tx fails then can be retried
				 */
				logger.debug("seller existing locked volume: {} {}, locked volume: {} {}",
						matchedOrder.getLockedVolume(), toCA, qtyTraded, toCA);
				double lockVol = matchedOrder.getLockedVolume() + qtyTraded;
				logger.debug("seller total locked volume: {} {}", lockVol, toCA);
				matchedOrder.setLockedVolume(lockVol);
				logger.debug("buyer existing locked volume: {} {}, locked volume: {} {}", orders.getLockedVolume(),
						pairCA, matchedOrder.getPrice() * qtyTraded, pairCA);
				// locking with trade fee
				lockVol = orders.getLockedVolume() + (matchedOrder.getPrice() * qtyTraded) + buyerTradeFee;
				logger.debug("buyer total locked volume: {} {}", lockVol, pairCA);
				orders.setLockedVolume(lockVol);
			} else {
				// order type is SELL
				// buyer is matched order's user
				buyer = matchedOrder.getUser();
				// seller is coming order's user
				seller = orders.getUser();
				/**
				 * Setting the locked volume of orders, if trade tx fails then can be retried
				 */
				logger.debug("buyer existing locked volume: {} {}, locked volume: {} {}",
						matchedOrder.getLockedVolume(), pairCA, matchedOrder.getPrice() * qtyTraded, pairCA);
				// locking with trade fee
				double lockVol = matchedOrder.getLockedVolume() + (matchedOrder.getPrice() * qtyTraded) + buyerTradeFee;
				logger.debug("buyer total locked volume: {} {}", lockVol, pairCA);
				matchedOrder.setLockedVolume(lockVol);
				logger.debug("buyer total locked volume after set: {} {}", matchedOrder.getLockedVolume(), pairCA);

				logger.debug("seller existing locked volume: {} {}, locked volume: {} {}", orders.getLockedVolume(),
						toCA, qtyTraded, toCA);
				lockVol = orders.getLockedVolume() + qtyTraded;
				logger.debug("seller total locked volume: {} {}", lockVol, toCA);
				orders.setLockedVolume(lockVol);
				logger.debug("seller total locked volume after set: {} {}", orders.getLockedVolume(), toCA);
			}
			// saving the processed BUY/SELL order in trade
			logger.debug("matched order id: {}", matchedOrder.getId());
			logger.debug("orders id: {}", orders.getId());
			Trade trade = new Trade(matchedOrder.getPrice(), qtyTraded, buyer, seller, marketCurrency, pairedCurrency,
					OrderStandard.LIMIT, buyerTradeFee, sellerTradeFee, matchedOrder, orders);
			trade = orderAsyncServices.saveTrade(trade);

			logger.debug("trade saved id: {} with matche orders id: {} ,requested order id: {}", trade.getId(),
					matchedOrder.getId(), orders.getId());
			transactionService.processTransaction(matchedOrder, orders, qtyTraded, buyer, seller, remainingVolume,
					buyerTradeFee, sellerTradeFee, trade);
		}
		return remainingVolume;
	}

	@Override
	public Page<Orders> getBuyOrdersListByPair(long marketCurrencyId, long pairedCurrencyId) {
		PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE, Direction.DESC, "price");
		return ordersRepository.findBuyOrderList(marketCurrencyId, pairedCurrencyId, OrderType.BUY,
				OrderStatus.SUBMITTED, pageRequest);
	}

	@Override
	public Page<Orders> getSellOrdersListByPair(long marketCurrencyId, long pairedCurrencyId) {
		PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE, Direction.DESC, "price");
		return ordersRepository.findSellOrderList(marketCurrencyId, pairedCurrencyId, OrderType.SELL,
				OrderStatus.SUBMITTED, pageRequest);
	}

	/**
	 * this will calculate the lowest selling price, thats why it is best buy for
	 * buyers
	 */
	@Override
	public Double getBestBuy(List<Orders> sellOrderList) {
		Double bestBuy = sellOrderList.get(0).getPrice();
		for (int i = 0; i < sellOrderList.size() - 1; i++) {
			if (bestBuy > sellOrderList.get(i + 1).getPrice()) {
				bestBuy = sellOrderList.get(i + 1).getPrice();
			}
		}
		return bestBuy;
	}

	/**
	 * this will calculate the highest selling price, thats why it is worst buy for
	 * buyers
	 */
	@Override
	public Double getWorstBuy(List<Orders> sellOrderList) {
		Double wrostBuy = sellOrderList.get(0).getPrice();
		for (int i = 0; i < sellOrderList.size() - 1; i++) {
			if (wrostBuy < sellOrderList.get(i + 1).getPrice()) {
				wrostBuy = sellOrderList.get(i + 1).getPrice();
			}
		}
		return wrostBuy;
	}

	/**
	 * this will calculate the highest buying price, thats why it is best sell for
	 * seller
	 */
	@Override
	public Double getBestSell(List<Orders> buyOrderList) {
		Double bestSell = null;
		for (int i = 0; i < buyOrderList.size() - 1; i++) {
			bestSell = buyOrderList.get(0).getPrice();
			if (bestSell < buyOrderList.get(i + 1).getPrice()) {
				bestSell = buyOrderList.get(i + 1).getPrice();
			}
		}
		return bestSell;
	}

	/**
	 * this will calculate the lowest buying price, thats why it is worst sell for
	 * seller
	 */
	@Override
	public Double getWorstSell(List<Orders> buyOrderList) {
		Double wrostSell = buyOrderList.get(0).getPrice();
		for (int i = 0; i < buyOrderList.size() - 1; i++) {
			if (wrostSell > buyOrderList.get(i + 1).getPrice()) {
				wrostSell = buyOrderList.get(i + 1).getPrice();
			}
		}
		return wrostSell;
	}

	@Override
	public Long countOrderByOrderTypeWithGreaterAndLesThan(OrderType orderType, Long marketCurrencyId,
			Long pairedCurrencyId, Double price) {
		if (orderType.equals(OrderType.BUY)) {
			return ordersRepository.countOrderByOrderTypeAndPriceGreaterThan(orderType, marketCurrencyId,
					pairedCurrencyId, price);
		} else {
			return ordersRepository.countOrderByOrderTypeAndPriceLessThan(orderType, marketCurrencyId, pairedCurrencyId,
					price);

		}
	}

	@Override
	public Long countOrderByOrderType(OrderType orderType) {
		return ordersRepository.countOrderByOrderType(orderType);
	}

	@Override
	public Orders matchedOrder(List<Orders> ordersList) {
		return ordersList.get(0);
	}

	/**
	 * 
	 */
	@Override
	public void removeOrderFromList(List<Orders> ordersList) {
		ordersList.remove(0);
	}

	/**
	 * 
	 */
	@Override
	public List<Orders> findOrdersListByUserAndOrderStatus(User user, OrderStatus orderStatus) {
		return ordersRepository.findByUserAndOrderStatus(user, orderStatus);
	}

	@Override
	public Page<Orders> findOrdersListByUserAndOrderStatus(int pageNumber, int pageSize, String sortOrder,
			String sortBy, User user, OrderStatus orderStatus) {
		Direction sort = Direction.DESC;
		if ("asc".equals(sortOrder)) {
			sort = Direction.ASC;
		}
		Pageable pageable = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return ordersRepository.findByUserAndOrderStatus(user, orderStatus, pageable);
	}

	@Override
	public double totalUserBalanceInBook(User user, Currency marketCurrency, Currency pairedCurrency) {
		List<Orders> toOrders = ordersRepository.findByUserAndOrderStatusAndOrderTypeAndMarketCurrency(user,
				OrderStatus.SUBMITTED, OrderType.BUY, marketCurrency);
		if (CurrencyType.ERC20TOKEN.equals(marketCurrency.getCurrencyType())) {
			toOrders = ordersRepository.findByUserAndOrderStatusAndOrderTypeAndMarketCurrency(user,
					OrderStatus.SUBMITTED, OrderType.SELL, marketCurrency);
		}
		List<Orders> fromOrders = ordersRepository.findByUserAndOrderStatusAndOrderTypeAndPairedCurrency(user,
				OrderStatus.SUBMITTED, OrderType.SELL, pairedCurrency);
		double total = 0.0;
		for (Orders orders : toOrders) {
			total = total + orders.getVolume();
		}
		for (Orders orders : fromOrders) {
			total = total + orders.getVolume();
		}
		return total;
	}

	@Override
	public Long countActiveOpenOrder() {

		Date endDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.DATE, -7);
		Date startDate = c.getTime();
		return ordersRepository.countOrdersByCreatedOnBetweenAndOrderStatus(startDate, endDate, OrderStatus.SUBMITTED);
	}

	@Override
	public Long getTotalCountOfNewerBuyerAndSeller(OrderType orderType) {
		Date endDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.DATE, -1);
		Date startDate = c.getTime();
		return ordersRepository.countOrderByOrderTypeAndCreatedOnBetween(orderType, startDate, endDate);
	}

	/**
	 * 
	 */
	@Override
	public Long countOrdersByOrderTypeAndUser(User user, OrderType orderType) {
		return ordersRepository.countOrderByUserAndOrderType(user, orderType);
	}

	@Override
	public Orders getOrderDetails(long orderId) {
		return ordersRepository.getOne(orderId);
	}

	/**
	 * 
	 */
	@Override
	public Page<Orders> getListOfLatestOrders(int pageNumber, int pageSize, String sortOrder, String sortBy) {
		Pageable page = new PageRequest(pageNumber, pageSize, Direction.DESC, sortBy);

		Date endDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.DATE, -1);
		Date startDate = c.getTime();
		return ordersRepository.findByOrderStatusAndCreatedOnBetween(OrderStatus.SUBMITTED, startDate, endDate, page);
	}

	@Override
	public boolean cancelOrder(long orderId) {
		Orders order = ordersRepository.findOne(orderId);
		if (order != null) {
			order.setOrderStatus(OrderStatus.CANCELLED);
			ordersRepository.save(order);
			return true;
		}
		return false;
	}

	@Override
	public double findUserOrderLockedVolume(User user, Currency marketCurrency, Currency pairedCurrency) {
		List<Orders> toOrders = ordersRepository.findByUserAndOrderStatusAndOrderTypeAndMarketCurrency(user,
				OrderStatus.COMPLETED, OrderType.SELL, marketCurrency);
		double total = 0.0;
		for (Orders orders : toOrders) {
			total = total + orders.getLockedVolume();
		}
		logger.debug("user : {} order locked volume: {}", user.getEmailId(), total);
		return total;
	}
}
