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
	public String getBalance(String ticker, String currencyType, User user);

	public String getPairedBalance(Orders orders, Currency marketCurrency, Currency pairedCurrency, double qtyTraded);
}
