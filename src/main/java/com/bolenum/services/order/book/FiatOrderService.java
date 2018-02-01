/**
 * 
 */
package com.bolenum.services.order.book;

import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.data.domain.Page;

import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;

/**
 * @author chandan kumar singh
 * @date 16-Nov-2017
 */
public interface FiatOrderService {

	/**
	 * @param orders
	 * @return
	 */
	public Orders createOrders(Orders orders);

	/**
	 * @param user
	 * @param orders
	 * @return
	 */
	public String checkFiatOrderEligibility(User user, Orders orders);

	/**
	 * @param matchedOrder
	 * @param orders
	 * @return
	 */
	public Orders processFiatOrderList(Orders matchedOrder, Orders orders);

	/**
	 * @param user
	 * @param orderStatus
	 * @param orderType
	 * @param currency
	 * @return
	 */
	double getPlacedOrderVolumeOfCurrency(User user, OrderStatus orderStatus, OrderType orderType, Currency currency);

	/**
	 * @param order
	 * @return
	 */
	public boolean processCancelOrder(Orders order);

	/**
	 * @param order
	 * @return
	 */
	public boolean buyerPaidConfirmtion(Orders order);

	/**
	 * @param order
	 * @param currencyAbr
	 * @param currencyType
	 * @return
	 */
	public Future<Boolean> processTransactionFiatOrders(Orders order, String currencyAbr, String currencyType);

	/**
	 * @param order
	 * @param page
	 * @param size
	 * @param marketCurrencyId
	 * @param pairedCurrencyId
	 * @return
	 */
	public Page<Orders> existingOrders(Orders order, int page, int size, long marketCurrencyId, long pairedCurrencyId);

	/**
	 * @param user
	 * @param marketCurrency
	 * @param pairedCurrency
	 * @return
	 */
	Map<String, String> byersWalletAddressAndCurrencyAbbr(User user, Currency marketCurrency, Currency pairedCurrency);
}