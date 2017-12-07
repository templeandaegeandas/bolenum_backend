/**
 * 
 */
package com.bolenum.services.user.wallet;

import java.util.concurrent.Future;

import com.bolenum.exceptions.InsufficientBalanceException;
import com.bolenum.model.Currency;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.fees.WithdrawalFee;

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

	String getWalletBalance(String uuid);

	/**
	 * @description getWalletAddress @param @return
	 *              Map<String,Object> @exception
	 * 
	 */
	String getWalletAddress(String walletUuid);

	boolean validateAddresss(String btcWalletUuid, String toAddress);

	boolean validateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount)
			throws InsufficientBalanceException;

	Transaction setDepositeList(Transaction transaction);

	boolean validateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount,
			WithdrawalFee withdrawalFee, Currency currency);

	Future<Boolean> withdrawAmount(String currencyType, String coinCode, User user, String toAddress, Double amount,
			Double bolenumFee, User admin);
}
