/**
 * 
 */
package com.bolenum.services.order.book;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.bolenum.constant.UrlConstant;
import com.bolenum.enums.CurrencyType;
import com.bolenum.enums.MessageType;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.services.user.notification.NotificationService;
import com.bolenum.services.user.trade.TradeTransactionService;
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
	private OrdersRepository ordersRepository;

	@Autowired
	private WalletService walletService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private TradeTransactionService tradeTransactionService;

	@Override
	public Orders createOrders(Orders orders) {
		if (OrderType.SELL.equals(orders.getOrderType())) {
			orderAsyncService.saveLastPrice(orders.getMarketCurrency(), orders.getPairedCurrency(), orders.getPrice());
		}
		return ordersRepository.save(orders);
	}

	/**
	 * to check the eligibility to place an order by checking available balance of
	 * crypto currencies #return "proceed" if user have sufficient balance #return
	 * "Synchronizing" if BTC block chain is syncing with network
	 */
	@Override
	public String checkFiatOrderEligibility(User user, Orders orders) {

		Currency currency = null;
		Currency marketCurrency = orders.getMarketCurrency();
		if (!(CurrencyType.FIAT.equals(marketCurrency.getCurrencyType()))) {
			currency = marketCurrency;
		}

		Currency pairedCurrency = orders.getPairedCurrency();
		if (!(CurrencyType.FIAT.equals(pairedCurrency.getCurrencyType()))) {
			currency = pairedCurrency;
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
	public Orders processFiatOrderList(Orders matchedOrder, Orders orders) {
		// fetching order type BUY or SELL
		// OrderType orderType = orders.getOrderType();
		User buyer = null, seller = null;

		Double qtyTraded;

		String msg = "";
		String msg1 = "";

		String toCurrency = orders.getMarketCurrency().getCurrencyAbbreviation();
		String pairCurr = orders.getPairedCurrency().getCurrencyAbbreviation();

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
			matchedOrder.setMatchedOn(new Date());
			logger.debug("locked volume after set: {}", matchedOrder.getLockedVolume());
			remainingVolume = 0.0;
			orders.setVolume(remainingVolume);
			orders.setLockedVolume(qtyTraded);
			orders.setOrderStatus(OrderStatus.LOCKED);
			orders.setMatchedOn(new Date());
			logger.debug("orders saving started");
			if (OrderType.BUY.equals(orders.getOrderType())) {
				orders.setMatchedOrder(matchedOrder);
				matchedOrder.setMatchedOrder(orders);
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
				orders.setMatchedOrder(matchedOrder);
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
			Currency marketCurrency) {
		List<Orders> orders = ordersRepository.findByUserAndOrderStatusAndOrderTypeAndMarketCurrency(user, orderStatus,
				orderType, marketCurrency);
		double total = 0.0;
		for (Orders order : orders) {
			total = total + order.getVolume() + order.getLockedVolume();
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
			matched.setMatchedOrder(null);
			matched.setOrderStatus(OrderStatus.SUBMITTED);
			matched.setMatchedOn(null);
			logger.debug("matched order saving start");
			orderAsyncService.saveOrder(matched);
		}
		order.setVolume(order.getVolume() + order.getLockedVolume());
		order.setLockedVolume(0);
		order.setOrderStatus(OrderStatus.CANCELLED);
		try {
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
		User buyer = null;
		User seller = null;
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
				exitingOrder.setConfirm(true);
				matched.setMatchedOn(new Date());
				ordersRepository.save(exitingOrder);
				ordersRepository.save(matched);
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("PAID_NOTIFICATION", MessageType.PAID_NOTIFICATION);
					jsonObject.put("matchedOrderId", matched.getId());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				simpMessagingTemplate.convertAndSend(
						UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + matched.getUser().getUserId(),
						jsonObject.toString());
				logger.debug("WebSocket message: {}", MessageType.ORDER_CONFIRMATION + "#" + matched.getId());
				return true;
			} else {
				logger.error("order is of SELL type");
			}

		}
		return false;
	}

	@Override
	@Async
	public Future<Boolean> processTransactionFiatOrders(Orders sellerOrder, String currencyAbr, String currencyType) {
		logger.debug("processTransactionFiatOrders order id: {}", sellerOrder.getId());
		logger.debug("processTransactionFiatOrders matched order: {}", sellerOrder.getMatchedOrder());
		logger.debug("processTransactionFiatOrders matched order id: {}", sellerOrder.getMatchedOrder().getId());
		Orders buyersOrder = ordersRepository.findOne(sellerOrder.getMatchedOrder().getId());
		logger.debug("buyers order: {}", buyersOrder);
		logger.debug("buyers order id: {}", buyersOrder.getId());
		if (buyersOrder.isConfirm()) {
			User buyer = buyersOrder.getUser();
			User seller = sellerOrder.getUser();
			double qtyTraded = sellerOrder.getLockedVolume();
			try {
				Boolean result = tradeTransactionService.performTradeTransaction(currencyAbr, currencyType, qtyTraded,
						buyer, seller, null);
				logger.debug("perform fiat transaction result: {} of sell order id: {} and buy order id:{}", result,
						sellerOrder.getId(), buyersOrder.getId());
				if (result) {
					sellerOrder.setOrderStatus(OrderStatus.COMPLETED);
					sellerOrder.setLockedVolume(0);
					sellerOrder.setConfirm(true);

					ordersRepository.save(sellerOrder);
					buyersOrder.setOrderStatus(OrderStatus.COMPLETED);
					ordersRepository.save(buyersOrder);
					Trade trade = new Trade(buyersOrder.getPrice(), qtyTraded, buyer, seller,
							sellerOrder.getMarketCurrency(), sellerOrder.getPairedCurrency(),
							sellerOrder.getOrderStandard(), 0.0, 0.0, null, null);
					orderAsyncService.saveTrade(trade);
				}
			} catch (Exception e) {
				return new AsyncResult<>(true);
			}
			return new AsyncResult<>(true);
		}
		return new AsyncResult<>(false);
	}

	@Override
	public Page<Orders> existingOrders(Orders order, int page, int size) {
		OrderType orderType = OrderType.BUY;
		Pageable pageable = new PageRequest(page, size, Direction.DESC, "price");
		if (OrderType.BUY.equals(order.getOrderType())) {
			orderType = OrderType.SELL;
			pageable = new PageRequest(page, size, Direction.ASC, "price");
			return ordersRepository
					.findByPriceLessThanEqualAndOrderTypeAndOrderStatusAndMarketCurrencyAndPairedCurrency(
							order.getPrice(), orderType, OrderStatus.SUBMITTED,
							order.getMarketCurrency(), order.getPairedCurrency(),
							pageable);
		}
		return ordersRepository
				.findByPriceGreaterThanEqualAndOrderTypeAndOrderStatusAndMarketCurrencyCurrencyIdAndPairedCurrencyCurrencyId(
						order.getPrice(), orderType, OrderStatus.SUBMITTED, order.getMarketCurrency().getCurrencyId(),
						order.getPairedCurrency().getCurrencyId(), pageable);
	}

	@Override
	public Map<String, String> byersWalletAddressAndCurrencyAbbr(User user, Currency marketCurrency,
			Currency pairedCurrency) {
		Map<String, String> map = new HashMap<>();
		String currencyAbbr = "";
		if (CurrencyType.FIAT.equals(marketCurrency.getCurrencyType())) {
			map.put("currencyAbbr", pairedCurrency.getCurrencyAbbreviation());
			currencyAbbr = pairedCurrency.getCurrencyAbbreviation();
		} else {
			map.put("currencyAbbr", marketCurrency.getCurrencyAbbreviation());
			currencyAbbr = marketCurrency.getCurrencyAbbreviation();
		}
		if (currencyAbbr.equals("BTC")) {
			map.put("address", user.getBtcWalletAddress());
		} else {
			map.put("address", user.getEthWalletaddress());
		}
		return map;
	}
}