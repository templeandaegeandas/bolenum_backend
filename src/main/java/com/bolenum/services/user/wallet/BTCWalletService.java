/**
 * 
 */
package com.bolenum.services.user.wallet;

import java.util.Map;

import com.bolenum.model.User;

import com.bolenum.exceptions.InsufficientBalanceException;
import com.bolenum.model.Transaction;

/**
 * @author chandan kumar singh
 * @date 22-Sep-2017
 */
public interface BTCWalletService {

	/**
	 * @description createHotWallet @param @return String @exception
	 * 
	 */
	String createHotWallet(String uuid);

	/**
	 * @description getWalletAddressAndQrCode @param @return
	 * List<String> @exception
	 *
	 */
	Map<String, Object> getWalletAddressAndQrCode(String uuid);

	String getWalletBalnce(String uuid);

	/**
	 * @description getWalletAddress @param @return
	 * Map<String,Object> @exception
	 * 
	 */
	String getWalletAddress(String walletUuid);

	boolean validateAddresss(String btcWalletUuid, String toAddress);

	boolean validateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount)
			throws InsufficientBalanceException;

	Transaction setDepositeList(Transaction transaction);

	boolean validateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount)
			throws InsufficientBalanceException;
}
