package com.bolenum.services.order.book;

import org.springframework.data.domain.Page;

import com.bolenum.model.User;
import com.bolenum.model.orders.book.Trade;

public interface TradeService {

	Page<Trade> getTradedOrdersLoggedIn(User user, int pageNumber, int pageSize, String sortOrder, String sortBy);

	Page<Trade> getTradedOrders(User user, int pageNumber, int pageSize, String sortOrder, String sortBy);

}
