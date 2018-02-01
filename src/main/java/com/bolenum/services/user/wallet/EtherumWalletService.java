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
	/**
	 * This method is use for cleint Version
	 * @return
	 */
	public String cleintVersion();

	
	/**
	 * This method is use to create Eth Wallet
	 * @param user
	 * @param tokenName
	 */
	void createEthWallet(User user, String tokenName);

	/**
	 * This method is use for eth Wallet Balance
	 * @param user
	 * @param tokenName
	 * @return
	 */
	UserCoin ethWalletBalance(User user, String tokenName);

	/**
	 * This method is use to get Eth WalletBalance For Admin
	 * @param userCoin
	 * @return
	 */
	Double getEthWalletBalanceForAdmin(UserCoin userCoin);
}
