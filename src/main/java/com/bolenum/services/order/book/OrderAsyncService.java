package com.bolenum.services.order.book;

import java.util.List;
import java.util.concurrent.Future;

import com.bolenum.model.Currency;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;

public interface OrderAsyncService {

	/**
	 * This method is use to save Order
	 * @param ordersList
	 * @return
	 */
	List<Orders> saveOrder(List<Orders> ordersList);

	/**
	 * This method is use to save Order
	 * @param orders
	 * @return
	 */
	Orders saveOrder(Orders orders);

	/**
	 * This method is use to save Trade
	 * @param tradeList
	 * @return
	 */
	List<Trade> saveTrade(List<Trade> tradeList);

	/**
	 * This method is use to save Trade
	 * @param trade
	 * @return
	 */
	Trade saveTrade(Trade trade);

	/**
	 * This method is to save Last Price
	 * @param marketCurrency
	 * @param pairedCurrency
	 * @param price
	 * @return
	 */
	Future<Boolean> saveLastPrice(Currency marketCurrency, Currency pairedCurrency, Double price);

}