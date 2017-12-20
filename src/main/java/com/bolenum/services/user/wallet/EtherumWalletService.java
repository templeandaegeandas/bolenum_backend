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
	 * 
	 * @description createWallet
	 * @param 
	 * @return String
	 * @exception 
	 *
	 */
	
//	@Async
//	public void createWallet(User user);
//	
	
	/**
	 * 
	 * @description cleintVersion
	 * @param 
	 * @return String
	 * @exception 
	 *
	 */
	public String cleintVersion();
	/**
	 * 
	 * @description getWalletBalance
	 * @param 
	 * @return Double
	 * @exception 
	 *
	 */
	//public Double getWalletBalance(User user);
	
	
	void createEthWallet(User user, String tokenName);
	
	
	UserCoin ethWalletBalance(User user, String tokenName);
	
	Double getEthWalletBalanceForAdmin(UserCoin userCoin);
}
