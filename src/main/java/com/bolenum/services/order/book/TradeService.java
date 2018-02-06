package com.bolenum.services.order.book;

import org.springframework.data.domain.Page;

import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Trade;

public interface TradeService {

	/**
	 * This method is use to get Traded Orders LoggedIn
	 * @param user
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @param orderType
	 * @param date
	 * @return
	 */
	Page<Trade> getTradedOrdersLoggedIn(User user, int pageNumber, int pageSize, String sortOrder, String sortBy,
			String orderType, Long date);

	/**
	 * This method is use to get Traded Orders
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @return
	 */
	Page<Trade> getTradedOrders(int pageNumber, int pageSize, String sortOrder, String sortBy);

	/**
	 * This method is use to get Traded Orders LoggedIn
	 * @param user
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	Page<Trade> getTradedOrdersLoggedIn(User user, int pageNumber, int pageSize);

	/**
	 * This method is use to find Total Trade Fee Of Currency
	 * @param currency
	 * @return
	 */
	Double findTotalTradeFeeOfCurrency(Currency currency);

}
