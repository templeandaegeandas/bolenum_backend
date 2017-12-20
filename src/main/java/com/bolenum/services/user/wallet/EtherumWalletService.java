/**
 * 
 */
package com.bolenum.services.user.wallet;

import com.bolenum.model.User;
import com.bolenum.model.coin.UserCoin;

/**
 * @author chandan kumar singh
 * @date 26-Sep-2017
 */
public interface EtherumWalletService {

	// @Async
	// public void createWallet(User user);
	//

	public String cleintVersion();
	// public Double getWalletBalance(User user);

	void createEthWallet(User user, String tokenName);

	UserCoin ethWalletBalance(User user, String tokenName);

	Double getEthWalletBalanceForAdmin(UserCoin userCoin);
}
