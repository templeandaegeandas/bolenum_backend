/**
 * 
 */
package com.bolenum.services.order.book;

import java.text.ParseException;

import com.bolenum.model.CurrencyPair;

/**
 * @author chandan kumar singh
 * @date 11-Oct-2017
 */
public interface MarketPriceService {

	long tradesIn24h(CurrencyPair pair) throws ParseException;

	Double ordersIn24hHigh(CurrencyPair pair) throws ParseException;

	Double ordersIn24hLow(CurrencyPair pair) throws ParseException;

	Double ordersIn24hVolume(CurrencyPair pair) throws ParseException;
	
}