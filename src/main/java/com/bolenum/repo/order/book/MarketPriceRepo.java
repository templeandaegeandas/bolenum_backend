/**
 * 
 */
package com.bolenum.repo.order.book;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.Currency;
import com.bolenum.model.orders.book.MarketPrice;

/**
 * @author chandan kumar singh
 * @date 11-Oct-2017
 */
public interface MarketPriceRepo extends JpaRepository<MarketPrice, Serializable> {

	/**
	 * @description findByCurrency
	 * @param 
	 * @return MarketPrice
	 * @exception 
	 * 
	 */
	MarketPrice findByCurrency(Currency currency);

}
