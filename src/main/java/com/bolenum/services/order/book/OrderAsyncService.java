package com.bolenum.services.order.book;

import java.util.List;

import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.PartialTrade;
import com.bolenum.model.orders.book.Trade;

public interface OrderAsyncService {

	List<Orders> saveOrder(List<Orders> ordersList);
	
	Orders saveOrder(Orders orders);

	List<Trade> saveTrade(List<Trade> tradeList);
	
	Trade saveTrade(Trade trade);
	
	List<PartialTrade> savePartialTrade(List<PartialTrade> tradeList);

}