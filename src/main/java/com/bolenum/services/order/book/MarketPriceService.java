/**
 * 
 */
package com.bolenum.services.order.book;

import java.text.ParseException;

import com.bolenum.model.Currency;

/**
 * @author chandan kumar singh
 * @date 11-Oct-2017
 */
public interface MarketPriceService {

	long tradesIn24h(Currency marketCurrency, Currency pairedCurrency) throws ParseException;

	Double ordersIn24hHigh(Currency marketCurrency, Currency pairedCurrency) throws ParseException;

	Double ordersIn24hLow(Currency marketCurrency, Currency pairedCurrency) throws ParseException;

	Double ordersIn24hVolume(Currency marketCurrency, Currency pairedCurrency) throws ParseException;

}