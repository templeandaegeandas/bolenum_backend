/**
 * 
 */
package com.bolenum.services.order.book;

import com.bolenum.model.Currency;
import com.bolenum.model.orders.book.MarketPrice;

/**
 * @author chandan kumar singh
 * @date 11-Oct-2017
 */
public interface MarketPriceService {
	public MarketPrice savePrice(MarketPrice marketPrice);

	public void priceFromCoinMarketCap();

	public MarketPrice findByCurrency(Currency currency);

	MarketPrice findByCurrencyId(String currencyAbbreviation);
}
