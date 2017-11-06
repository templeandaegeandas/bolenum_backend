package com.bolenum.services.order.book;

import java.util.List;

import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;

public interface OrderAsyncService {

	//@Async
	List<Orders> saveOrder(List<Orders> ordersList);

	//@Async
	List<Trade> saveTrade(List<Trade> tradeList);

}
