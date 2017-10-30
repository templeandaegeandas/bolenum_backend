package com.bolenum.services.order.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.bolenum.model.User;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.TradeRepository;

@Service
public class TradeServiceImpl implements TradeService {

	@Autowired
	private TradeRepository tradeRepository;
	
	@Override
	public Page<Trade> getTradedOrdersLoggedIn(User user, int pageNumber, int pageSize, String sortOrder, String sortBy) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return tradeRepository.findByBuyerIdOrSellerId(user.getUserId(), user.getUserId(), pageRequest);
	}
	
	@Override
	public Page<Trade> getTradedOrders(User user, int pageNumber, int pageSize, String sortOrder, String sortBy) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return tradeRepository.findAll(pageRequest);
	}
}
