/**
 * 
 */
package com.bolenum.services.order.book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.repo.order.book.TradeRepository;

/**
 * @author Vishal Kumar
 * @date 02-Jan-2018
 */
@Service
@Transactional
public class MarketPriceServiceImpl implements MarketPriceService {

	private Logger logger = LoggerFactory.getLogger(MarketPriceServiceImpl.class);

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private TradeRepository tradeRepository;

	private final String DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";

	@Override
	public long tradesIn24h(long marketCurrencyId, long pairedCurrencyId) throws ParseException {
		long countTrade24h = 0;
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMATE);
		Instant now = Instant.now();
		Instant before = now.minus(Duration.ofHours(24));
		Date dateBefore = Date.from(before);
		String dateBeforeString = dateFormatter.format(dateBefore);
		logger.debug("date before string: {}", dateFormatter.parse(dateBeforeString));
		countTrade24h = tradeRepository.count24hTrade(marketCurrencyId, pairedCurrencyId,
				dateFormatter.parse(dateBeforeString));
		return countTrade24h;
	}

	@Override
	public Double ordersIn24hHigh(long marketCurrencyId, long pairedCurrencyId) throws ParseException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMATE);
		Instant now = Instant.now();
		Instant before = now.minus(Duration.ofHours(24));
		Date dateBefore = Date.from(before);
		String dateBeforeString = dateFormatter.format(dateBefore);
		Double high24h = ordersRepository.ordersIn24hHigh(marketCurrencyId, pairedCurrencyId,
				dateFormatter.parse(dateBeforeString));
		return high24h == null ? 0.0 : high24h;
	}

	@Override
	public Double ordersIn24hLow(long marketCurrencyId, long pairedCurrencyId) throws ParseException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMATE);
		Instant now = Instant.now();
		Instant before = now.minus(Duration.ofHours(24));
		Date dateBefore = Date.from(before);
		String dateBeforeString = dateFormatter.format(dateBefore);
		Double low24h = ordersRepository.ordersIn24hLow(marketCurrencyId, pairedCurrencyId,
				dateFormatter.parse(dateBeforeString));
		return low24h == null ? 0.0 : low24h;
	}

	@Override
	public Double ordersIn24hVolume(long marketCurrencyId, long pairedCurrencyId) throws ParseException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMATE);
		Instant now = Instant.now();
		Instant before = now.minus(Duration.ofHours(24));
		Date dateBefore = Date.from(before);
		String dateBeforeString = dateFormatter.format(dateBefore);
		Double volume24h = ordersRepository.ordersIn24hVolume(marketCurrencyId, pairedCurrencyId,
				dateFormatter.parse(dateBeforeString));
		return volume24h == null ? 0.0 : volume24h;
	}
}