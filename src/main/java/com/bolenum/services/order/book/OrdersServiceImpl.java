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
import com.bolenum.enums.OrderStandard;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.PartialTrade;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.services.admin.CurrencyPairService;
import com.bolenum.services.user.notification.NotificationService;
import com.bolenum.services.user.transactions.TransactionService;
import com.bolenum.services.user.wallet.WalletService;

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
	private CurrencyPairService currencyPairService;

	/*
	 * @Autowired private MarketPriceService marketPriceService;
	 */

	@Autowired
	private WalletService walletService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private FiatOrderService fiatOrderService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	public static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);

	List<Orders> ordersList = new ArrayList<>();

	List<Orders> matchedOrdersList = new ArrayList<>();

	List<Trade> tradeList = new ArrayList<>();

	List<PartialTrade> partialTradeList = new ArrayList<>();

	/**
	 * this will check user wallet balance to get place an order
	 */
	@Override
	public String checkOrderEligibility(User user, Orders orders, Long pairId) {
		CurrencyPair currencyPair = currencyPairService.findCurrencypairByPairId(pairId);
		orders.setPair(currencyPair);
		String tickter = null, minOrderVol = null, currencyType = null;
		Currency currency;
		/**
		 * if order type is SELL then only checking, user have selling volume
		 */
		if (OrderType.SELL.equals(orders.getOrderType())) {
			currency = currencyPair.getToCurrency().get(0);
			tickter = currency.getCurrencyAbbreviation();
			currencyType = currency.getCurrencyType().toString();
			minOrderVol = String.valueOf(orders.getVolume());
		} else {
			minOrderVol = getPairedBalance(orders, currencyPair, orders.getVolume());
			currency = currencyPair.getPairedCurrency().get(0);
			tickter = currency.getCurrencyAbbreviation();
			currencyType = currency.getCurrencyType().toString();
		}
		double userPlacedOrderVolume = fiatOrderService.getPlacedOrderVolumeOfCurrency(user, OrderStatus.SUBMITTED,
				OrderType.SELL, currency);
		logger.debug("user placed order volume: {} and order volume: {}", userPlacedOrderVolume, minOrderVol);
		double minBalance = Double.valueOf(minOrderVol) + userPlacedOrderVolume;
		logger.debug("minimum order volume required to buy/sell: {}", minBalance);
		// getting the user current wallet balance
		String balance = walletService.getBalance(tickter, currencyType, user);
		balance = balance.replace("BTC", "");
		if (!balance.equals("Synchronizing") || !balance.equals("null")) {
			// user must have balance then user is eligible for placing order
			if (Double.valueOf(balance) > 0 && (Double.valueOf(balance) >= Double.valueOf(minBalance))) {
				balance = "proceed";
			}
		}
		return balance;
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
			total = total + order.getVolume();
		}
		return total;
	}

	@Override
	public Boolean processOrder(Orders orders) throws InterruptedException, ExecutionException {
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
	 * @return #true if user requested order is matched with own existing user
	 *         else #false
	 */
	@Override
	public boolean isUsersSelfOrder(Orders reqOrder, List<Orders> orderList) {
		if (orderList.size() > 0) {
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
		CurrencyPair pair = orders.getPair();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		if (OrderType.BUY.equals(orderType)) {
			List<Orders> sellOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndPairOrderByPriceAsc(OrderType.SELL, OrderStatus.SUBMITTED, pair);
			/**
			 * checking user self order, return false if self order else
			 * proceed.
			 */

			if (isUsersSelfOrder(orders, sellOrderList)) {
				return processed;
			}
			while (sellOrderList.size() > 0 && remainingVolume > 0) {
				logger.debug("inner buy while loop for buyers remainingVolume: {}", remainingVolume);
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders, pair);
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
			List<Orders> buyOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndPairOrderByPriceDesc(OrderType.BUY, OrderStatus.SUBMITTED, pair);
			/**
			 * checking user self order, return false if self order else
			 * proceed. checking user self order, return false if self order
			 * else proceed.
			 */
			if (isUsersSelfOrder(orders, buyOrderList)) {
				return processed;
			}
			logger.debug("buyOrderList.size(): {}", buyOrderList.size());
			while (buyOrderList.size() > 0 && remainingVolume > 0) {
				logger.debug("inner sell while loop for sellers remainingVolume: {}", remainingVolume);
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders, pair);
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
		}
		logger.debug("MarketOrder: Order list saving started");
		/**
		 * if any exception occurs then clear list, otherwise double order will
		 * be placed
		 */
		try {
			orderAsyncServices.saveOrder(ordersList);
		} catch (Exception e) {
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
		CurrencyPair pair = orders.getPair();
		logger.debug("Order type is equal with buy: {}", orderType.equals(OrderType.BUY));
		// checking the order type is BUY
		if (OrderType.BUY.equals(orderType)) {
			// fetching the seller list whose selling price is less than equal
			// to buying price
			List<Orders> sellOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndPairAndPriceLessThanEqualOrderByPriceAsc(OrderType.SELL,
							OrderStatus.SUBMITTED, pair, price);
			/**
			 * checking user self order, return false if self order else
			 * proceed.
			 */

			if (isUsersSelfOrder(orders, sellOrderList)) {
				return processed;
			}
			/**
			 * fetch one best seller's price from list of sellers, order by
			 * price in ASC then process the order
			 */
			while (sellOrderList.size() > 0 && (remainingVolume > 0) && (price >= getBestBuy(sellOrderList))) {
				logger.debug("inner buy while loop for buyers and remaining volume: {}", remainingVolume);
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders, pair);
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
			 * fetching the list of BUYERS whose buy price is greater than sell
			 * price
			 */
			List<Orders> buyOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndPairAndPriceGreaterThanEqualOrderByPriceDesc(OrderType.BUY,
							OrderStatus.SUBMITTED, pair, price);
			/**
			 * checking user self order, return false if self order else
			 * proceed.
			 */
			if (isUsersSelfOrder(orders, buyOrderList)) {
				return processed;
			}

			/**
			 * fetch one best buyer's price from list of buyers, order by price
			 * in desc then process the order
			 */
			while (buyOrderList.size() > 0 && (remainingVolume > 0) && (price <= buyOrderList.get(0).getPrice())) {
				logger.debug("inner sell while loop for seller and remaining volume: {}", remainingVolume);
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders, pair);
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
	public Double processOrderList(List<Orders> ordersList, Double remainingVolume, Orders orders, CurrencyPair pair)
			throws InterruptedException, ExecutionException {
		// fetching order type BUY or SELL
		OrderType orderType = orders.getOrderType();
		User buyer, seller;
		logger.debug("process order list remainingVolume: {}", remainingVolume);
		// process till order size and remaining volume is > 0
		while ((ordersList.size() > 0) && (remainingVolume > 0)) {
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
				double remain = matchedOrder.getVolume() - remainingVolume;
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
				remainingVolume = remainingVolume - qtyTraded;
				logger.debug("remaining volume else: {}", remainingVolume);
				// removed processed order
				removeOrderFromList(ordersList);
				// new volume of processed order is 0
				matchedOrder.setVolume(0.0);
				// status of processed order is completed
				matchedOrder.setOrderStatus(OrderStatus.COMPLETED);
				// adding to order list by setting the new volume and status of
				// processed order
				// ordersList.add(matchedOrder);
				logger.debug("matching buy/sell completed");
			}
			// checking the order type BUY
			if (OrderType.BUY.equals(orderType)) {
				// buyer is coming order's user
				buyer = orders.getUser();
				// seller is matched order's user
				seller = matchedOrder.getUser();
			} else {
				// order type is SELL
				// buyer is matched order's user
				buyer = matchedOrder.getUser();
				// seller is coming order's user
				seller = orders.getUser();
			}
			// buyer and seller must be different user
			logger.debug("byuer id: {} seller id: {}", buyer.getUserId(), seller.getUserId());
			if (buyer.getUserId() != seller.getUserId()) {
				// saving the processed BUY/SELL order in trade
				Trade trade = new Trade(matchedOrder.getPrice(), qtyTraded, buyer, seller, pair, OrderStandard.LIMIT);
				tradeList.add(trade);
				logger.debug("trade added to tradelist");
				processTransaction(matchedOrder, orders, qtyTraded, buyer, seller, remainingVolume);
			}
		}
		logger.debug("tradeList saving started");
		orderAsyncServices.saveTrade(tradeList);
		tradeList.clear();
		logger.debug("tradeList saving finished");
		return remainingVolume;
	}

	/**
	 * @description processTransaction
	 * @param orders,qtyTraded,buyer,seller
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private void processTransaction(Orders matchedOrder, Orders orders, double qtyTraded, User buyer, User seller,
			double remainingVolume) throws InterruptedException, ExecutionException {
		String msg = "", msg1 = "";
		logger.debug("buyer: {} and seller: {} for order: {}", buyer.getEmailId(), seller.getEmailId(),
				matchedOrder.getId());
		// finding currency pair
		CurrencyPair currencyPair = currencyPairService.findCurrencypairByPairId(matchedOrder.getPair().getPairId());
		String[] tickters = new String[2];
		// finding the currency abbreviations
		tickters[0] = currencyPair.getToCurrency().get(0).getCurrencyAbbreviation();
		tickters[1] = currencyPair.getPairedCurrency().get(0).getCurrencyAbbreviation();
		// fetching the limit price of order
		String qtr = getPairedBalance(matchedOrder, currencyPair, qtyTraded);
		logger.debug("paired currency volume: {}, {}", qtr, tickters[1]);
		// checking the order type BUY
		if (OrderType.BUY.equals(orders.getOrderType())) {
			logger.debug("BUY Order");
			msg = "Hi " + buyer.getFirstName() + ", Your " + orders.getOrderType()
					+ " order has been initiated, quantity: " + qtyTraded + " " + tickters[0] + ", on " + qtr + " "
					+ tickters[1] + " remaining voloume: " + remainingVolume + " " + tickters[0];
			logger.debug("msg: {}", msg);
			msg1 = "Hi " + seller.getFirstName() + ", Your " + matchedOrder.getOrderType()
					+ " order has been initiated, quantity: " + qtr + " " + tickters[1] + ", on " + qtyTraded + " "
					+ tickters[0] + " remaining voloume: " + matchedOrder.getVolume() + " " + tickters[1];
			logger.debug("msg1: {}", msg1);
		} else {
			logger.debug("SELL Order");
			msg1 = "Hi " + seller.getFirstName() + ", Your " + orders.getOrderType()
					+ " order has been initiated, quantity: " + qtyTraded + " " + tickters[0] + ", on " + qtr + " "
					+ tickters[1] + " remaining voloume: " + remainingVolume + " " + tickters[0];
			logger.debug("msg1: {}", msg1);
			msg = "Hi " + buyer.getFirstName() + ", Your " + matchedOrder.getOrderType()
					+ " order has been initiated, quantity: " + qtr + " " + tickters[1] + ", on " + qtyTraded + " "
					+ tickters[0] + " remaining voloume: " + matchedOrder.getVolume() + " " + tickters[1];
			logger.debug("msg: {}", msg);
		}

		if (qtr != null && Double.valueOf(qtr) > 0) {
			// process tx buyers and sellers
			transactionService.performTransaction(tickters[0], qtyTraded, buyer, seller);
			sendNotification(seller, msg1);
			notificationService.saveNotification(seller, buyer, msg1);
			// process tx sellers and buyers
			transactionService.performTransaction(tickters[1], Double.valueOf(qtr), seller, buyer);
			sendNotification(buyer, msg);
			notificationService.saveNotification(buyer, seller, msg);
		} else {
			logger.debug("transaction processing failed due to paired currency volume");
		}
	}

	private boolean sendNotification(User user, String msg) {
		return notificationService.sendNotification(user, msg);
	}

	@Override
	public String getPairedBalance(Orders orders, CurrencyPair currencyPair, double qtyTraded) {
		String minBalance = null;
		/**
		 * if order type is BUY then for Market order, user should have total
		 * market price, for Limit order user should have volume (volume *
		 * price), price limit given by user
		 */
		if (OrderStandard.LIMIT.equals(orders.getOrderStandard())) {
			logger.debug("limit order buy on price: {}, {} and quantity traded: {}, {} ", orders.getPrice(),
					currencyPair.getPairedCurrency().get(0).getCurrencyAbbreviation(), qtyTraded,
					currencyPair.getToCurrency().get(0).getCurrencyAbbreviation());
			minBalance = String.valueOf(qtyTraded * orders.getPrice());
		} else {
			/**
			 * fetching the market BTC price of buying currency
			 */

			// MarketPrice marketPrice =
			// marketPriceService.findByCurrency(currencyPair.getPairedCurrency().get(0));
			/**
			 * 1 UNIT buying currency price in BTC Example 1 ETH = 0.0578560
			 * BTC, this will update according to order selling book
			 */
			double buyingCurrencyValue = currencyPair.getPairedCurrency().get(0).getPriceBTC();
			logger.debug("order value : {}, buyingCurrencyValue: {}", qtyTraded, buyingCurrencyValue);
			if (buyingCurrencyValue > 0) {
				/**
				 * user must have this balance to give market order, Example
				 * user want to BUY 3 BTC on market price, at this moment 1 ETH
				 * = 0.0578560 BTC then for 3 BTC (3/0.0578560) BTC, then user
				 * must have 51.852876106 ETH to buy 3 BTC
				 */
				minBalance = String.valueOf(qtyTraded / buyingCurrencyValue);
			}
		}
		return minBalance;
	}

	@Override
	public Page<Orders> getBuyOrdersListByPair(Long pairId) {
		CurrencyPair pair = currencyPairService.findCurrencypairByPairId(pairId);
		PageRequest pageRequest = new PageRequest(0, 10, Direction.DESC, "price");
		Page<Orders> orderBook = ordersRepository.findBuyOrderList(pair, OrderType.BUY, OrderStatus.SUBMITTED,
				pageRequest);
		return orderBook;
	}

	@Override
	public Page<Orders> getSellOrdersListByPair(Long pairId) {
		CurrencyPair pair = currencyPairService.findCurrencypairByPairId(pairId);
		PageRequest pageRequest = new PageRequest(0, 10, Direction.DESC, "price");
		Page<Orders> orderBook = ordersRepository.findSellOrderList(pair, OrderType.SELL, OrderStatus.SUBMITTED,
				pageRequest);
		return orderBook;
	}

	/**
	 * this will calculate the lowest selling price, thats why it is best buy
	 * for buyers
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
	 * this will calculate the highest selling price, thats why it is worst buy
	 * for buyers
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
	 * this will calculate the highest buying price, thats why it is best sell
	 * for seller
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
	 * this will calculate the lowest buying price, thats why it is worst sell
	 * for seller
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
	public Long countOrderByOrderTypeWithGreaterAndLesThan(OrderType orderType, Long pairId, Double price) {
		CurrencyPair pair = currencyPairService.findCurrencypairByPairId(pairId);
		if (orderType.equals("BUY")) {
			return ordersRepository.countOrderByOrderTypeAndPriceGreaterThan(orderType, pair, price);
		} else {
			return ordersRepository.countOrderByOrderTypeAndPriceLessThan(orderType, pair, price);

		}
	}

	@Override
	public Long countOrderByOrderType(OrderType orderType) {
		return ordersRepository.countOrderByOrderType(orderType);
	}

	@Override
	public Orders matchedOrder(List<Orders> ordersList) {
		Orders matchedOrder = ordersList.get(0);
		return matchedOrder;
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
	public double totalUserBalanceInBook(User user, Currency toCurrency, Currency pairedCurrency) {
		List<Orders> toOrders = ordersRepository.findByUserAndOrderStatusAndOrderTypeAndPairToCurrency(user,
				OrderStatus.SUBMITTED, OrderType.SELL, toCurrency);
		List<Orders> fromOrders = ordersRepository.findByUserAndOrderStatusAndOrderTypeAndPairPairedCurrency(user,
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
		startDate = (Date) startDate;
		return ordersRepository.countOrdersByCreatedOnBetween(startDate, endDate);
	}

	@Override
	public Long getTotalCountOfNewerBuyerAndSeller(OrderType orderType) {
		Date endDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.DATE, -1);
		Date startDate = c.getTime();
		startDate = (Date) startDate;
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
		Pageable page  = new PageRequest(pageNumber, pageSize, Direction.DESC, sortBy);

		Date endDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.DATE, -1);
		Date startDate = c.getTime();
		startDate = (Date) startDate;
		//return ordersRepository.findByCreatedOnBetween(page,startDate,endDate);
		return ordersRepository.findByCreatedOnBetween( startDate, endDate,page);
	}
}
