package com.bolenum.services.order.book;

import org.springframework.data.domain.Page;

import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Trade;

public interface TradeService {

	Page<Trade> getTradedOrdersLoggedIn(User user, int pageNumber, int pageSize, String sortOrder, String sortBy,
			String orderType, Long date);

	Page<Trade> getTradedOrders(int pageNumber, int pageSize, String sortOrder, String sortBy);

	Page<Trade> getTradedOrdersLoggedIn(User user, int pageNumber, int pageSize);

	Double findTotalTradeFeeOfCurrency(Currency currency);

}
