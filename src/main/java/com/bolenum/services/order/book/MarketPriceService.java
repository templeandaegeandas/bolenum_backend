/**
 * 
 */
package com.bolenum.services.order.book;

import java.text.ParseException;

/**
 * @author chandan kumar singh
 * @date 11-Oct-2017
 */
public interface MarketPriceService {

	/**
	 * This method is use to trades In 24h
	 * @param marketCurrencyId
	 * @param pairedCurrencyId
	 * @return
	 * @throws ParseException
	 */
	long tradesIn24h(long marketCurrencyId, long pairedCurrencyId) throws ParseException;

	/**
	 * This method is use to orders In 24h High
	 * @param marketCurrencyId
	 * @param pairedCurrencyId
	 * @return
	 * @throws ParseException
	 */
	Double ordersIn24hHigh(long marketCurrencyId, long pairedCurrencyId) throws ParseException;

	/**
	 * This method is use to orders In 24h Low
	 * @param marketCurrencyId
	 * @param pairedCurrencyId
	 * @return
	 * @throws ParseException
	 */
	Double ordersIn24hLow(long marketCurrencyId, long pairedCurrencyId) throws ParseException;

	/**
	 * This method is use for orders In 24h Volume
	 * @param marketCurrencyId
	 * @param pairedCurrencyId
	 * @return
	 * @throws ParseException
	 */
	Double ordersIn24hVolume(long marketCurrencyId, long pairedCurrencyId) throws ParseException;

}