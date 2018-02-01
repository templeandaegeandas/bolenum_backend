/**
 * 
 */
package com.bolenum.services.user.wallet;

import java.util.concurrent.Future;

import com.bolenum.model.Currency;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.coin.Erc20Token;
import com.bolenum.model.fees.WithdrawalFee;

/**
 * @author chandan kumar singh
 * @date 22-Sep-2017
 */
public interface BTCWalletService {

	/**
	 * This method is use to create Btc Account
	 * @param uuid
	 * @return
	 */
	String createBtcAccount(String uuid);

	/**
	 * THis method is use to get BtcAccount Address 
	 * @param walletUuid
	 * @return
	 */
	String getBtcAccountAddress(String walletUuid);

	/**
	 * This method is use to validate Btc Addresss
	 * @param btcWalletUuid
	 * @param toAddress
	 * @return
	 */
	boolean validateBtcAddresss(String btcWalletUuid, String toAddress);

	/**
	 * This method is use to validate Erc20 Withdraw Amount
	 * @param user
	 * @param tokenName
	 * @param withdrawAmount
	 * @param withdrawalFee
	 * @param toAddress
	 * @return
	 */
	boolean validateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount, WithdrawalFee withdrawalFee,
			String toAddress);

	/**
	 * This method is use to set Deposite List
	 * @param transaction
	 * @return
	 */
	Transaction setDepositeList(Transaction transaction);

	/**
	 * This method is use to withdraw Amount
	 * @param currencyType
	 * @param coinCode
	 * @param user
	 * @param toAddress
	 * @param amount
	 * @param bolenumFee
	 * @param admin
	 * @return
	 */
	Future<Boolean> withdrawAmount(String currencyType, String coinCode, User user, String toAddress, Double amount,
			Double bolenumFee, User admin);

	/**
	 * This method is use to get Btc Account Balance
	 * @param uuid
	 * @return
	 */
	String getBtcAccountBalance(String uuid);

	/**
	 * This method is use to get Bolenum Btc Account Balance
	 * @return
	 */
	String getBolenumBtcAccountBalance();

	/**
	 * This method is use for admin Withdraw Crypto Amount
	 * @param user
	 * @param tokenName
	 * @param withdrawAmount
	 * @param toAddress
	 * @return
	 */
	boolean adminWithdrawCryptoAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	/**
	 * This method is use for admin Withdraw Erc20Token Amount
	 * @param user
	 * @param tokenName
	 * @param withdrawAmount
	 * @param toAddress
	 * @return
	 */
	Future<Boolean> adminWithdrawErc20TokenAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	/**
	 * This method is use for admin Validate Crypto Withdraw Amount
	 * @param user
	 * @param tokenName
	 * @param withdrawAmount
	 * @param toAddress
	 * @return
	 */
	boolean adminValidateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	/**
	 * This method is use for admin Validate Erc20 Withdraw Amount
	 * @param user
	 * @param tokenName
	 * @param withdrawAmount
	 * @param toAddress
	 * @param erc20Token
	 * @return
	 */
	boolean adminValidateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress,
			Erc20Token erc20Token);

	/**
	 * This method is use to validate Crypto Withdraw Amount
	 * @param user
	 * @param tokenName
	 * @param withdrawAmount
	 * @param withdrawalFee
	 * @param currency
	 * @param toAddress
	 * @return
	 */
	boolean validateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount,
			WithdrawalFee withdrawalFee, Currency currency, String toAddress);
}
