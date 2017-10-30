package com.bolenum.services.order.book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.bolenum.enums.OrderStandard;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.MarketPrice;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.services.admin.CurrencyPairService;
import com.bolenum.services.user.transactions.TransactionService;
import com.bolenum.services.user.wallet.BTCWalletService;
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

	@Autowired
	private BTCWalletService bTCWalletService;

	@Autowired
	private MarketPriceService marketPriceService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private WalletService walletService;

	public static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);

	List<Orders> ordersList = new ArrayList<>();

	List<Trade> tradeList = new ArrayList<>();

	/**
	 * this will check user wallet balance to get place an order
	 */
	@Override
	public String checkOrderEligibility(User user, Orders orders, Long pairId) {
		CurrencyPair currencyPair = currencyPairService.findCurrencypairByPairId(pairId);
		orders.setPair(currencyPair);
		String tickter = null, minOrderVol = null;
		/**
		 * if order type is SELL then only checking, user have selling volume
		 */
		if (orders.getOrderType().equals(OrderType.SELL)) {
			tickter = currencyPair.getToCurrency().get(0).getCurrencyAbbreviation();
			minOrderVol = String.valueOf(orders.getVolume());
		} else {
			minOrderVol = getPairedBalance(orders, currencyPair, orders.getVolume());
			tickter = currencyPair.getPairedCurrency().get(0).getCurrencyAbbreviation();
		}
		double userPlacedOrderVolume = getPlacedOrderVolume(user);
		logger.debug("user placed order volume: {} and order volume: {}", userPlacedOrderVolume, minOrderVol);
		double minBalance = Double.valueOf(minOrderVol) + userPlacedOrderVolume;
		logger.debug("minimum order volume required to buy/sell: {}", minBalance);
		// getting the user current wallet balance
		String balance = walletService.getBalance(tickter, user);
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
	private double getPlacedOrderVolume(User user) {
		List<Orders> orders = findOrdersListByUserAndOrderStatus(user, OrderStatus.SUBMITTED);
		double total = 0.0;
		for (Orders order : orders) {
			total = total + order.getVolume();
		}
		return total;
	}

	@Override
	public Boolean processOrder(Orders orders) {
		if (orders.equals(OrderStandard.MARKET)) {
			logger.debug("Processing market order");
			return processMarketOrder(orders);
		} else {
			logger.debug("Processing limit order");
			return processLimitOrder(orders);
		}
	}

	@Override
	public Orders deleteOrder(Long ordersId) {
		Orders orders = ordersRepository.findOne(ordersId);
		orders.setDeletedOn(new Date());
		orders.setOrderStatus(OrderStatus.CANCELLED);
		return ordersRepository.save(orders);
	}

	/**
	 * process market order
	 */
	@Override
	public Boolean processMarketOrder(Orders orders) {
		Boolean processed = false;
		OrderType orderType = orders.getOrderType();
		CurrencyPair pair = orders.getPair();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		if (orderType.equals(OrderType.BUY)) {
			List<Orders> sellOrderList = ordersRepository.findByOrderTypeAndOrderStatusAndPairOrderByPriceAsc(
					OrderType.SELL, OrderStatus.SUBMITTED, pair);
			while (sellOrderList.size() > 0 && remainingVolume > 0) {
				logger.debug("inner buy while loop for buyers remainingVolume: {}", remainingVolume);
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				ordersList.add(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		} else {
			List<Orders> buyOrderList = ordersRepository.findByOrderTypeAndOrderStatusAndPairOrderByPriceDesc(
					OrderType.BUY, OrderStatus.SUBMITTED, pair);
			while (buyOrderList.size() > 0 && remainingVolume > 0) {
				logger.debug("inner sell while loop for sellers remainingVolume: {}", remainingVolume);
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				ordersList.add(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		}
		orderAsyncServices.saveOrder(ordersList);
		return processed;
	}

	/**
	 * process limit order
	 */
	@Override
	public Boolean processLimitOrder(Orders orders) {
		Boolean processed = false;
		OrderType orderType = orders.getOrderType();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		Double price = orders.getPrice();
		CurrencyPair pair = orders.getPair();
		logger.debug("Order type is equal with buy: {}", orderType.equals(OrderType.BUY));
		// checking the order type is BUY
		if (orderType.equals(OrderType.BUY)) {
			// fetching the seller list whose selling price is less than equal
			// to buying price
			List<Orders> sellOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndPairAndPriceLessThanEqualOrderByPriceAsc(OrderType.SELL,
							OrderStatus.SUBMITTED, pair, price);
			/**
			 * fetch one best seller's price from list of sellers, order by
			 * price in ASC then process the order
			 */
			while (sellOrderList.size() > 0 && (remainingVolume > 0) && (price >= getBestBuy(sellOrderList))) {
				logger.debug("inner buy while loop for buyers and remaining volume: {}", remainingVolume);
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
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
			 * fetch one best buyer's price from list of buyers, order by price
			 * in desc then process the order
			 */
			while (buyOrderList.size() > 0 && (remainingVolume > 0) && (price <= buyOrderList.get(0).getPrice())) {
				logger.debug("inner sell while loop for seller and remaining volume: {}", remainingVolume);
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				ordersList.add(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		}
		orderAsyncServices.saveOrder(ordersList);
		return processed;
	}

	/**
	 * process the buyers and sellers order
	 * 
	 */
	@Override
	public Double processOrderList(List<Orders> ordersList, Double remainingVolume, Orders orders) {
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
				logger.debug("qty traded else: ", qtyTraded);
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
				ordersList.add(matchedOrder);
				logger.debug("matching buy/sell completed");
			}
			// checking the order type BUY
			if (orderType.equals(OrderType.BUY)) {
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
			if (buyer != seller) {
				// saving the processed BUY/SELL order in trade
				Trade trade = new Trade(matchedOrder.getPrice(), qtyTraded, buyer, seller, OrderStandard.LIMIT);
				tradeList.add(trade);
				logger.debug("saving trade completed");
				processTransaction(matchedOrder, qtyTraded, buyer, seller);
			}
		}
		orderAsyncServices.saveTrade(tradeList);
		return remainingVolume;
	}

	/**
	 * @description processTransaction
	 * @param orders,qtyTraded,buyerId,sellerId
	 */
	private void processTransaction(Orders orders, double qtyTraded, User buyer, User seller) {
		// finding buyer
//		User buyer = userService.findByUserId(buyerId);
		// finding seller
//		User seller = userService.findByUserId(sellerId);
		logger.debug("buyer: {} and seller: {} for order: {}", buyer.getEmailId(), seller.getEmailId(), orders.getId());
		// finding currency pair
		CurrencyPair currencyPair = currencyPairService.findCurrencypairByPairId(orders.getPair().getPairId());
		String[] tickters = new String[2];
		// finding the currency abbreviations
		tickters[0] = currencyPair.getToCurrency().get(0).getCurrencyAbbreviation();
		tickters[1] = currencyPair.getPairedCurrency().get(0).getCurrencyAbbreviation();
		// fetching the limit price of order
		String qtr = getPairedBalance(orders, currencyPair, qtyTraded);
		logger.debug("other qtr: {}", qtr);
		if (qtr != null) {
			// process tx buyers and sellers
			process(tickters[0], qtyTraded, buyer, seller);
			// process tx sellers and buyers
			process(tickters[1], Double.valueOf(qtr), seller, buyer);
		}
	}

	private boolean process(String currencyAbr, double qtyTraded, User buyer, User seller) {
		switch (currencyAbr) {
		case "BTC":
			boolean status = transactionService.performBtcTransaction(seller,
					bTCWalletService.getWalletAddress(buyer.getBtcWalletUuid()), qtyTraded);
			logger.debug("is BTC transaction successed: {}", status);
			break;
		case "ETH":
			status = transactionService.performEthTransaction(seller, buyer.getEthWalletaddress(), qtyTraded);
			logger.debug("is ETH transaction successed: {}", status);
			break;
		}
		return true;
	}

	@Override
	public String getPairedBalance(Orders orders, CurrencyPair currencyPair, double qtyTraded) {
		String minBalance = null;
		/**
		 * if order type is BUY then for Market order, user should have total
		 * market price, for Limit order user should have volume (volume *
		 * price), price limit given by user
		 */
		if (orders.getOrderStandard().equals(OrderStandard.LIMIT)) {
			logger.debug("limit order buy on price: {}", orders.getPrice());
			minBalance = String.valueOf(qtyTraded * orders.getPrice());
		} else {
			/**
			 * fetching the market BTC price of buying currency
			 */
			MarketPrice marketPrice = marketPriceService.findByCurrency(currencyPair.getPairedCurrency().get(0));
			/**
			 * 1 UNIT buying currency price in BTC Example 1 ETH = 0.0578560
			 * BTC, this will update according to order selling book
			 */
			Double buyingCurrencyValue = marketPrice.getPriceBTC();
			logger.debug("order value : {}, buyingCurrencyValue: {}", qtyTraded, buyingCurrencyValue);
			if (marketPrice != null && buyingCurrencyValue != null) {
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
		Page<Orders> orderBook = ordersRepository.findBuyOrderList(pair, OrderType.BUY, OrderStatus.SUBMITTED, pageRequest);
		return orderBook;
	}

	@Override
	public Page<Orders> getSellOrdersListByPair(Long pairId) {
		CurrencyPair pair = currencyPairService.findCurrencypairByPairId(pairId);
		PageRequest pageRequest = new PageRequest(0, 10, Direction.DESC, "price");
		Page<Orders> orderBook = ordersRepository.findSellOrderList(pair, OrderType.SELL, OrderStatus.SUBMITTED, pageRequest);
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

	@Override
	public void removeOrderFromList(List<Orders> ordersList) {
		ordersList.remove(0);
	}

	@Override
	public List<Orders> findOrdersListByUserAndOrderStatus(User user, OrderStatus orderStatus) {
		return ordersRepository.findByUserAndOrderStatus(user, orderStatus);
	}
}
