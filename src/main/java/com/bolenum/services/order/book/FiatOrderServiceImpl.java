/**
 * 
 */
package com.bolenum.services.order.book;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.enums.CurrencyType;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.services.admin.CurrencyPairService;
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
		if (!(toCurrency.getCurrencyType().equals(CurrencyType.FIAT))) {
			currency = toCurrency;
		}

		Currency pairCurrency = currencyPair.getPairedCurrency().get(0);
		if (!(pairCurrency.getCurrencyType().equals(CurrencyType.FIAT))) {
			currency = pairCurrency;
		}
		String tickter = null, minOrderVol = null, currencyType = null;
		/**
		 * if order type is SELL then only checking, user have selling volume
		 */
		if (orders.getOrderType().equals(OrderType.SELL)) {
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
	public boolean processFiatOrderList(Orders matchedOrder, Orders orders, CurrencyPair pair) {
		// fetching order type BUY or SELL
		// OrderType orderType = orders.getOrderType();
		// User buyer, seller;
		if (!(pair.getToCurrency().get(0).getCurrencyType().equals(CurrencyType.FIAT)
				|| pair.getPairedCurrency().get(0).getCurrencyType().equals(CurrencyType.FIAT))) {
			return false;
		}
		Double qtyTraded;
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
			matchedOrder.setVolume(remain);
			logger.debug("reamining volume after set: {}", matchedOrder.getVolume());
			matchedOrder.setLockedVolume(qtyTraded);
			logger.debug("locked volume after set: {}", matchedOrder.getLockedVolume());
			remainingVolume = 0.0;
			orders.setVolume(remainingVolume);
			orders.setLockedVolume(qtyTraded);
			orders.setOrderStatus(OrderStatus.LOCKED);
			logger.debug("orders saving started");
			orderAsyncService.saveOrder(orders);
			logger.debug("orders saving finished and matched order saving started");
			orderAsyncService.saveOrder(matchedOrder);
			logger.debug("matched order saving finished");
			return true;
		}
		return false;
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
}
