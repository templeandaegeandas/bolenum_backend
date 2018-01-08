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

	long tradesIn24h(long marketCurrencyId, long pairedCurrencyId) throws ParseException;

	Double ordersIn24hHigh(long marketCurrencyId, long pairedCurrencyId) throws ParseException;

	Double ordersIn24hLow(long marketCurrencyId, long pairedCurrencyId) throws ParseException;

	Double ordersIn24hVolume(long marketCurrencyId, long pairedCurrencyId) throws ParseException;

}