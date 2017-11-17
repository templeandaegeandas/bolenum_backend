/**
 * 
 */
package com.bolenum.services.user.wallet;

import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 17-Oct-2017
 */
public interface WalletService {
	public String getBalance(String ticker, String currencyType, User user);
}