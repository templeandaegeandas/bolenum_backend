/**
 * 
 */
package com.bolenum.services.order.book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bolenum.constant.UrlConstant;
import com.bolenum.enums.CurrencyType;
import com.bolenum.enums.MessageType;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.services.admin.CurrencyPairService;
import com.bolenum.services.user.notification.NotificationService;
import com.bolenum.services.user.transactions.TransactionService;
import com.bolenum.services.user.wallet.WalletService;

/**
 * @author chandan kumar singh
 * @date 16-Nov-2017
 */
@Service
public class FiatOrderServiceImpl implements FiatOrderService {
	private Logger logger = LoggerFactory.getLogger(FiatOrderServiceImpl.class);
	@Autowired
	private OrderAsyncService orderAsyncService;

	@Autowired
	private CurrencyPairService currencyPairService;

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private WalletService walletService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private TransactionService transactionService;

	@Override
	public Orders createOrders(Orders orders) {
		return ordersRepository.save(orders);
	}

	/**
	 * to check the eligibility to place an order by checking available balance
	 * of crypto currencies #return "proceed" if user have sufficient balance
	 * #return "Synchronizing" if BTC block chain is syncing with network
	 */
	@Override
	public String checkFiatOrderEligibility(User user, Orders orders, long pairId) {
		CurrencyPair currencyPair = currencyPairService.findCurrencypairByPairId(pairId);
		orders.setPair(currencyPair);

		Currency currency = null;
		Currency toCurrency = currencyPair.getToCurrency().get(0);
		if (!(CurrencyType.FIAT.equals(toCurrency.getCurrencyType()))) {
			currency = toCurrency;
		}

		Currency pairCurrency = currencyPair.getPairedCurrency().get(0);
		if (!(CurrencyType.FIAT.equals(pairCurrency.getCurrencyType()))) {
			currency = pairCurrency;
		}
		String tickter = null, minOrderVol = null, currencyType = null;
		/**
		 * if order type is SELL then only checking, user have selling volume
		 */
		if (OrderType.SELL.equals(orders.getOrderType())) {
			minOrderVol = String.valueOf(orders.getVolume());
		} else {
			minOrderVol = "0";
		}
		tickter = currency.getCurrencyAbbreviation();
		currencyType = currency.getCurrencyType().toString();
		double userPlacedOrderVolume = getPlacedOrderVolumeOfCurrency(user, OrderStatus.SUBMITTED, OrderType.SELL,
				currency);
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

	@Override
	public Orders processFiatOrderList(Orders matchedOrder, Orders orders, CurrencyPair pair) {
		// fetching order type BUY or SELL
		// OrderType orderType = orders.getOrderType();
		User buyer = null, seller = null;

		Double qtyTraded;

		String msg = "", msg1 = "";

		String toCurrency = pair.getToCurrency().get(0).getCurrencyAbbreviation();
		String pairCurr = pair.getPairedCurrency().get(0).getCurrencyAbbreviation();

		double remainingVolume = orders.getVolume();
		logger.debug("process order list remainingVolume: {}", remainingVolume);
		// process till order size and remaining volume is > 0
		if (remainingVolume == matchedOrder.getVolume()) {
			// qtyTraded is total selling/buying volume
			qtyTraded = remainingVolume;
			logger.debug("qty traded: {}", qtyTraded);
			// setting new required SELL/BUY volume is remaining order
			// volume
			double remain = matchedOrder.getVolume() - remainingVolume;
			logger.debug("reamining volume: {}", remain);
			matchedOrder.setOrderStatus(OrderStatus.LOCKED);
			matchedOrder.setVolume(remain);
			logger.debug("reamining volume after set: {}", matchedOrder.getVolume());
			matchedOrder.setLockedVolume(qtyTraded);
			logger.debug("locked volume after set: {}", matchedOrder.getLockedVolume());
			remainingVolume = 0.0;
			orders.setVolume(remainingVolume);
			orders.setLockedVolume(qtyTraded);
			orders.setOrderStatus(OrderStatus.LOCKED);

			logger.debug("orders saving started");
			if (OrderType.BUY.equals(orders.getOrderType())) {
				orders.setMatchedOrder(matchedOrder);
				buyer = orders.getUser();
				seller = matchedOrder.getUser();
				msg = "Hi " + buyer.getFirstName() + ", Your " + orders.getOrderType()
						+ " order has been locked,  quantity: " + qtyTraded + " " + toCurrency + ", on "
						+ qtyTraded * orders.getPrice() + " " + pairCurr + " with " + seller.getFirstName();
				logger.debug("msg: {}", msg);
				msg1 = "Hi " + seller.getFirstName() + ", Your " + matchedOrder.getOrderType()
						+ " order has been locked, quantity: " + qtyTraded + " " + toCurrency + ", on "
						+ qtyTraded * orders.getPrice() + " " + pairCurr + " with " + buyer.getFirstName();
				logger.debug("msg1: {}", msg1);
			}
			orders = orderAsyncService.saveOrder(orders);
			if (OrderType.SELL.equals(orders.getOrderType())) {
				matchedOrder.setMatchedOrder(orders);
				buyer = matchedOrder.getUser();
				seller = orders.getUser();

				msg1 = "Hi " + seller.getFirstName() + ", Your " + orders.getOrderType()
						+ " order has been locked, quantity: " + qtyTraded + " " + toCurrency + ", on "
						+ qtyTraded * orders.getPrice() + " " + pairCurr + " with " + buyer.getFirstName();
				logger.debug("msg1: {}", msg1);
				msg = "Hi " + buyer.getFirstName() + ", Your " + matchedOrder.getOrderType()
						+ " order has been locked, quantity: " + qtyTraded + " " + toCurrency + ", on "
						+ qtyTraded * orders.getPrice() + " " + pairCurr + " with " + seller.getFirstName();
				logger.debug("msg: {}", msg);
			}
			logger.debug("orders saving finished and matched order saving started");
			orderAsyncService.saveOrder(matchedOrder);
			logger.debug("matched order saving finished");
			notificationService.sendNotification(seller, msg1);
			notificationService.saveNotification(seller, buyer, msg1);
			notificationService.sendNotification(buyer, msg);
			notificationService.saveNotification(buyer, seller, msg);
			return orders;
		}
		return orders;
	}

	/**
	 * to get the sum of placed order volume of paired currency
	 * 
	 * @param user
	 * @param order
	 *            status
	 * @param order
	 *            type
	 * @param currency
	 * @return sum of order
	 */
	@Override
	public double getPlacedOrderVolumeOfCurrency(User user, OrderStatus orderStatus, OrderType orderType,
			Currency currency) {
		List<Orders> orders = ordersRepository.findByUserAndOrderStatusAndOrderTypeAndPairToCurrency(user, orderStatus,
				orderType, currency);
		double total = 0.0;
		for (Orders order : orders) {
			total = total + order.getVolume();
		}
		return total;
	}

	@Override
	@Transactional
	public boolean processCancelOrder(Orders order) {
		Orders matched = order.getMatchedOrder();
		if (matched != null) {
			matched.setVolume(matched.getVolume() + matched.getLockedVolume());
			matched.setLockedVolume(0);
			matched.setOrderStatus(OrderStatus.SUBMITTED);
		}
		order.setVolume(order.getVolume() + order.getLockedVolume());
		order.setLockedVolume(0);
		order.setMatchedOrder(null);
		order.setOrderStatus(OrderStatus.CANCELLED);
		try {
			logger.debug("matched order saving start");
			orderAsyncService.saveOrder(matched);
			logger.debug("matched order saving completed and order saving started");
			orderAsyncService.saveOrder(order);
			logger.debug("order saving completed");
		} catch (Exception e) {
			logger.error("cancel order saving failed: {}", e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * to send message to seller, buyer has paid
	 */
	@Override
	public boolean buyerPaidConfirmtion(Orders exitingOrder) {
		Orders matched = exitingOrder.getMatchedOrder();
		String msg = "";
		User buyer = null, seller = null;
		if (matched != null) {
			if (OrderType.BUY.equals(exitingOrder.getOrderType())) {
				buyer = exitingOrder.getUser();
				seller = matched.getUser();
				msg = "Hi " + matched.getUser().getFirstName() + " your " + matched.getOrderType() + " is in process, "
						+ exitingOrder.getUser().getFirstName() + " has paid you the amount:"
						+ exitingOrder.getLockedVolume() * exitingOrder.getPrice()
						+ " Please confirm amount by login into bolenumexchage.";
				notificationService.sendNotification(seller, msg);
				notificationService.saveNotification(seller, buyer, msg);
				matched.setConfirm(true);
				ordersRepository.save(matched);
				simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_ORDER_BUYER_CONFIRM,
						MessageType.ORDER_CONFIRMATION + "#" + matched.getId());
				return true;
			} else {
				logger.error("order is of SELL type");
			}

		}
		return false;
	}

	@Override
	@Transactional
	@Async
	public boolean processTransactionFiatOrders(Orders sellerOrder) {
		Orders buyersOrder = ordersRepository.findByMatchedOrder(sellerOrder);
		String currencyAbr = null;
		if (!(CurrencyType.FIAT.equals(sellerOrder.getPair().getToCurrency().get(0).getCurrencyType()))) {
			currencyAbr = sellerOrder.getPair().getToCurrency().get(0).getCurrencyAbbreviation();
		} else {
			currencyAbr = sellerOrder.getPair().getPairedCurrency().get(0).getCurrencyAbbreviation();
		}
		if (buyersOrder != null) {
			User buyer = buyersOrder.getUser();
			User seller = sellerOrder.getUser();
			double qtyTraded = sellerOrder.getLockedVolume();
			try {
				Future<Boolean> result = transactionService.performTransaction(currencyAbr, qtyTraded, buyer, seller);
				boolean res = result.get();
				logger.debug("perform fiat transaction result: {} of sell order id: {} and buy order id:{}", res,
						sellerOrder.getId(), buyersOrder.getId());
				if (res) {
					buyersOrder.setLockedVolume(0);
					buyersOrder.setMatchedOrder(null);
					buyersOrder.setOrderStatus(OrderStatus.COMPLETED);

					sellerOrder.setOrderStatus(OrderStatus.COMPLETED);
					sellerOrder.setLockedVolume(0);

					ordersRepository.save(sellerOrder);
					ordersRepository.save(buyersOrder);
					Trade trade = new Trade(buyersOrder.getPrice(), qtyTraded, buyer, seller, sellerOrder.getPair(),
							sellerOrder.getOrderStandard());
					orderAsyncService.saveTrade(trade);
				}
			} catch (InterruptedException | ExecutionException e) {
				logger.error("perform fiat transaction failed: {}", e.getMessage());
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	public Page<Orders> existingOrders(Orders order, long pairId, int page, int size) {
		OrderType orderType = OrderType.BUY;
		Pageable pageable = new PageRequest(page, size, Direction.ASC, "price");
		if (OrderType.BUY.equals(order.getOrderType())) {
			orderType = OrderType.SELL;
			pageable = new PageRequest(page, size, Direction.DESC, "price");
			return ordersRepository.findByPriceGreaterThanEqualAndOrderTypeAndOrderStatusAndPairPairId(
					order.getPrice(), orderType, OrderStatus.SUBMITTED, pairId, pageable);
		}
		return ordersRepository.findByPriceLessThanEqualAndOrderTypeAndOrderStatusAndPairPairId(
				order.getPrice(), orderType, OrderStatus.SUBMITTED, pairId, pageable);
	}

	@Override
	public Map<String, String> byersWalletAddressAndCurrencyAbbr(User user, CurrencyPair pair) {
		Map<String, String> map = new HashMap<String, String>();
		String currencyAbbr = "";
		if (CurrencyType.FIAT.equals(pair.getToCurrency().get(0).getCurrencyType())) {
			map.put("currencyAbbr", pair.getPairedCurrency().get(0).getCurrencyAbbreviation());
			currencyAbbr = pair.getPairedCurrency().get(0).getCurrencyAbbreviation();
		} else {
			map.put("currencyAbbr", pair.getToCurrency().get(0).getCurrencyAbbreviation());
			currencyAbbr = pair.getToCurrency().get(0).getCurrencyAbbreviation();
		}
		if (currencyAbbr.equals("BTC")) {
			map.put("address", user.getBtcWalletAddress());
		} else {
			map.put("address", user.getEthWalletaddress());
		}
		return map;
	}
}
