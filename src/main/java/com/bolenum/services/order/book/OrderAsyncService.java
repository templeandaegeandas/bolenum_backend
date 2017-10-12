package com.bolenum.services.order.book;

import java.util.List;

import org.springframework.scheduling.annotation.Async;

import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;

public interface OrderAsyncService {

	@Async
	List<Orders> saveOrder(List<Orders> ordersList);

	@Async
	List<Trade> saveTrade(List<Trade> tradeList);

}
