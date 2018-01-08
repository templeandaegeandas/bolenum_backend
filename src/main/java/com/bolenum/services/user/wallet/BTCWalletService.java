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

	String createBtcAccount(String uuid);

	String getBtcAccountAddress(String walletUuid);

	boolean validateBtcAddresss(String btcWalletUuid, String toAddress);

	boolean validateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount, WithdrawalFee withdrawalFee,
			String toAddress);

	Transaction setDepositeList(Transaction transaction);

	Future<Boolean> withdrawAmount(String currencyType, String coinCode, User user, String toAddress, Double amount,
			Double bolenumFee, User admin);

	String getBtcAccountBalance(String uuid);

	String getBolenumBtcAccountBalance();

	boolean adminWithdrawCryptoAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	Future<Boolean> adminWithdrawErc20TokenAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	boolean adminValidateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	boolean adminValidateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress,
			Erc20Token erc20Token);

	boolean validateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount,
			WithdrawalFee withdrawalFee, Currency currency, String toAddress);
}
