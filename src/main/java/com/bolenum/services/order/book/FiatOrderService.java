/**
 * 
 */
package com.bolenum.services.order.book;

import java.util.Map;

import org.springframework.data.domain.Page;

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

	public Orders createOrders(Orders orders);

	public String checkFiatOrderEligibility(User user, Orders orders, long pairId);

	public Orders processFiatOrderList(Orders matchedOrder, Orders orders, CurrencyPair pair);

	double getPlacedOrderVolumeOfCurrency(User user, OrderStatus orderStatus, OrderType orderType, Currency currency);

	public boolean processCancelOrder(Orders order);

	public boolean buyerPaidConfirmtion(Orders order);

	public boolean processTransactionFiatOrders(Orders order);

	public Page<Orders> existingOrders(Orders order, int page, int size);

	Map<String, String> byersWalletAddressAndCurrencyAbbr(User user, CurrencyPair pair);
}
