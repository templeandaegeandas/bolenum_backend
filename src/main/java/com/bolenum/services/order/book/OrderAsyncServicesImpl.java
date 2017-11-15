package com.bolenum.services.order.book;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.repo.order.book.TradeRepository;

@Service
public class OrderAsyncServicesImpl implements OrderAsyncService{

	@Autowired
	private OrdersRepository ordersRepository;
	
	@Autowired
	private TradeRepository tradeRepository;
	
//	@Async
	@Override
	public List<Orders> saveOrder(List<Orders> ordersList) {
		return ordersRepository.save(ordersList);
	}
	
//	@Async
	@Override
	public List<Trade> saveTrade(List<Trade> tradeList) {
		return tradeRepository.save(tradeList);
	}
}
