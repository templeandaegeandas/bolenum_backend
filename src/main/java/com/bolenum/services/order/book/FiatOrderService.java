/**
 * 
 */
package com.bolenum.services.order.book;

import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;

/**
 * @author chandan kumar singh
 * @date 16-Nov-2017
 */
public interface FiatOrderService {
	String checkFiatOrderEligibility(User user, Orders orders, long pairId);

	public Orders processFiatOrderList(Orders matchedOrder, Orders orders, CurrencyPair pair);

	double getPlacedOrderVolumeOfCurrency(User user, OrderStatus orderStatus, OrderType orderType, Currency currency);

	public boolean processCancelOrder(Orders order);

	public boolean buyerPaidConfirmtion(Orders order);

	public boolean processTransactionFiatOrders(Orders order);
}
