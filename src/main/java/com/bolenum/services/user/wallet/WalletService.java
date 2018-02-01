/**
 * 
 */
package com.bolenum.services.user.wallet;

import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;

/**
 * @author chandan kumar singh
 * @date 17-Oct-2017
 */
public interface WalletService {
	/**
	 * This method is use to get Balance
	 * @param ticker
	 * @param currencyType
	 * @param user
	 * @return
	 */
	public String getBalance(String ticker, String currencyType, User user);

	
	/**
	 * This method is use to get Paired Balance
	 * @param orders
	 * @param marketCurrency
	 * @param pairedCurrency
	 * @param qtyTraded
	 * @return
	 */
	public String getPairedBalance(Orders orders, Currency marketCurrency, Currency pairedCurrency, double qtyTraded);
}
