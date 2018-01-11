package com.bolenum.services.order.book;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.TradeRepository;

@Service
public class TradeServiceImpl implements TradeService {

	public static final Logger logger = LoggerFactory.getLogger(TradeServiceImpl.class);

	@Autowired
	private TradeRepository tradeRepository;

	@Override
	public Page<Trade> getTradedOrdersLoggedIn(User user, int pageNumber, int pageSize, String sortOrder, String sortBy,
			String orderType, Long date) {
		Direction sort;
		final String timeZone = "GMT+5:30";
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		if (orderType.equals("buy")) {
			if (date != null) {
				Date startDate = new Date(date);
				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(TimeZone.getTimeZone(timeZone));
				cal.setTime(startDate);
				cal.set(Calendar.HOUR_OF_DAY, 00);
				cal.set(Calendar.MINUTE, 00);
				cal.set(Calendar.SECOND, 00);
				startDate = cal.getTime();
				cal.setTime(startDate);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				Date endDate = cal.getTime();
				logger.info("requested date buy: {}, start date: {} ,end date: {}", date, startDate, endDate);
				return tradeRepository.getByBuyerWithDate(user, startDate, endDate, pageRequest);
			} else {
				return tradeRepository.findByBuyer(user, pageRequest);
			}
		} else if (orderType.equals("sell")) {
			if (date != null) {
				Date startDate = new Date(date);
				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(TimeZone.getTimeZone(timeZone));
				cal.setTime(startDate);
				cal.set(Calendar.HOUR_OF_DAY, 00);
				cal.set(Calendar.MINUTE, 00);
				cal.set(Calendar.SECOND, 00);
				startDate = cal.getTime();
				cal.setTime(startDate);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				Date endDate = cal.getTime();
				logger.info("requested date: {}, start date: {} ,end date: {}", date, startDate, endDate);
				return tradeRepository.getBySellerWithDate(user, startDate, endDate, pageRequest);
			} else {
				return tradeRepository.findBySeller(user, pageRequest);
			}
		} else {
			if (date != null) {
				Date startDate = new Date(date);
				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(TimeZone.getTimeZone(timeZone));
				cal.setTime(startDate);
				cal.set(Calendar.HOUR_OF_DAY, 00);
				cal.set(Calendar.MINUTE, 00);
				cal.set(Calendar.SECOND, 00);
				startDate = cal.getTime();
				cal.setTime(startDate);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				Date endDate = cal.getTime();
				logger.info("requested date: {}, start date: {} ,end date: {}", date, startDate, endDate);
				return tradeRepository.getByBuyerOrSellerWithDate(user, user, startDate, endDate, pageRequest);
			} else {
				return tradeRepository.findByBuyerOrSeller(user, user, pageRequest);
			}
		}
	}

	@Override
	public Page<Trade> getTradedOrders(int pageNumber, int pageSize, String sortOrder, String sortBy) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return tradeRepository.findAll(pageRequest);
	}

	@Override
	public Page<Trade> getTradedOrdersLoggedIn(User user, int pageNumber, int pageSize) {
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, Direction.DESC, "createdOn");
		return tradeRepository.findByBuyerOrSeller(user, user, pageRequest);
	}

	@Override
	public Double findTotalTradeFeeOfCurrency(Currency currency) {
		List<Trade> trades = tradeRepository.findByMarketCurrency(currency);
		Double fee = 0.0;
		for (Trade trade : trades) {
			fee = trade.getSellerTradeFee() + trade.getBuyerTradeFee();
		}
		return fee;
	}
}
