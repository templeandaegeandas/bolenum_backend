
package com.bolenum.controller.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.WithdrawBalanceForm;
import com.bolenum.exceptions.InsufficientBalanceException;
import com.bolenum.model.Currency;
import com.bolenum.model.Erc20Token;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.fees.WithdrawalFee;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.admin.Erc20TokenService;
import com.bolenum.services.admin.fees.WithdrawalFeeService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.user.UserService;
import com.bolenum.services.user.wallet.BTCWalletService;
import com.bolenum.services.user.wallet.EtherumWalletService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * @author chandan kumar singh
 * @date 22-Sep-2017
 */
@RestController
@Api("Btc wallet controller")
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
public class BTCWalletController {

	@Autowired
	private LocaleService localService;

	@Autowired
	private BTCWalletService btcWalletService;

	@Autowired
	private EtherumWalletService etherumWalletService;

	@Autowired
	private Erc20TokenService erc20TokenService;

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private WithdrawalFeeService withdrawalFeeService;

	@Autowired
	private UserService userService;

	@Value("${admin.email}")
	private String adminEmail;

	private Logger logger = LoggerFactory.getLogger(BTCWalletController.class);

	/**
	 * to get the wallet address and QR code for get deposited in the
	 * wallet @description getWalletAddressAndQrCode @param coin code @return
	 * ResponseEntity<Map<String,Object>> @exception
	 *
	 */
	@RequestMapping(value = UrlConstant.DEPOSIT, method = RequestMethod.GET)
	public ResponseEntity<Object> getWalletAddressAndBalance(@RequestParam(name = "currencyType") String currencyType,
			@RequestParam(name = "code") String coinCode) {
		logger.debug("currency Type: {}, code:{}", currencyType, coinCode);
		if (coinCode == null || coinCode.isEmpty()) {
			throw new IllegalArgumentException(localService.getMessage("invalid.coin.code"));
		}
		User user = GenericUtils.getLoggedInUser(); // logged in user
		Map<String, Object> map = new HashMap<>();
		switch (currencyType) {
		case "CRYPTO":

			switch (coinCode) {
			case "BTC":
				Map<String, Object> mapAddressAndBal = new HashMap<>();
				mapAddressAndBal.put("address", btcWalletService.getWalletAddress(user.getBtcWalletUuid()));
				mapAddressAndBal.put("balance", btcWalletService.getWalletBalance(user.getBtcWalletUuid()));
				map.put("data", mapAddressAndBal);
				break;
			case "ETH":
				Double balance = etherumWalletService.getWalletBalance(user);
				Map<String, Object> mapAddress = new HashMap<>();
				mapAddress.put("address", user.getEthWalletaddress());
				mapAddress.put("balance", balance);
				map.put("data", mapAddress);
				break;
			default:
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("invalid.coin.code"), null);
			}
			break;
		case "ERC20TOKEN":
			Erc20Token erc20Token = erc20TokenService.getByCoin(coinCode);
			Double balance = erc20TokenService.getErc20WalletBalance(user, erc20Token);
			if (balance == null) {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("There is an error for getting balance of user for: " + coinCode),
						null);
			}
			Map<String, Object> mapAddress = new HashMap<>();
			mapAddress.put("address", user.getEthWalletaddress());
			mapAddress.put("balance", GenericUtils.getDecimalFormat(balance));
			map.put("data", mapAddress);
			break;
		case "FIAT":
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.coin.code"),
					null);
		default:
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.coin.code"),
					null);
		}
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), map);
	}

	/**
	 * 
	 * @param currencyAbbreviation
	 * @return
	 */
	@RequestMapping(value = UrlConstant.MARKET_PRICE, method = RequestMethod.GET)
	public ResponseEntity<Object> getBtcToEthPrice(@RequestParam("symbol") String currencyAbbreviation) {
		Currency marketPrice = currencyService.findByCurrencyAbbreviation(currencyAbbreviation);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), marketPrice);
	}

	/**
	 * 
	 * @param currencyType
	 * @param withdrawBalanceForm
	 * @param coinCode
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws InsufficientBalanceException
	 */
	@RequestMapping(value = UrlConstant.WITHDRAW, method = RequestMethod.POST)
	public ResponseEntity<Object> withdrawAmount(@RequestParam(name = "currencyType") String currencyType,
			@Valid @RequestBody WithdrawBalanceForm withdrawBalanceForm, @RequestParam(name = "code") String coinCode,
			BindingResult bindingResult) throws InterruptedException, ExecutionException {
		if (bindingResult.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localService.getMessage("withdraw.invalid.amount"), Optional.empty());
		}
		if (coinCode == null || coinCode.isEmpty()) {
			throw new IllegalArgumentException(localService.getMessage("invalid.coin.code"));
		}
		User user = GenericUtils.getLoggedInUser(); // logged in user
		User admin = userService.findByEmail(adminEmail);
		Currency currency = currencyService.findByCurrencyAbbreviation(coinCode);
		/**
		 * getting currency minimum withdraw amount
		 */
		WithdrawalFee withdrawalFee = withdrawalFeeService.getWithdrawalFee(currency.getCurrencyId());
		double bolenumFee = withdrawalFee.getFee();
		boolean validWithdrawAmount = false;
		switch (currencyType) {
		case "CRYPTO":
			validWithdrawAmount = btcWalletService.validateCryptoWithdrawAmount(user, coinCode,
					withdrawBalanceForm.getWithdrawAmount(), withdrawalFee, currency);
			logger.debug("Validate balance: {}", validWithdrawAmount);
			if (validWithdrawAmount) {
				btcWalletService.withdrawAmount(currencyType, coinCode, user, withdrawBalanceForm.getToAddress(),
						withdrawBalanceForm.getWithdrawAmount(), bolenumFee, admin);
			}
			/*
			 * switch (coinCode) { case "BTC":
			 * logger.debug("Validate balance: {}", validWithdrawAmount); if
			 * (validWithdrawAmount) {
			 * transactionService.performBtcTransaction(user,
			 * withdrawBalanceForm.getToAddress(),
			 * withdrawBalanceForm.getWithdrawAmount(),
			 * TransactionStatus.WITHDRAW, bolenumFee); if (bolenumFee > 0) {
			 * transactionService.performBtcTransaction(user,
			 * admin.getBtcWalletAddress(), bolenumFee, TransactionStatus.FEE,
			 * null); } } break; case "ETH":
			 * logger.debug("Validate balance: {}", validWithdrawAmount); if
			 * (validWithdrawAmount) { Future<Boolean> res =
			 * transactionService.performEthTransaction(user,
			 * withdrawBalanceForm.getToAddress(),
			 * withdrawBalanceForm.getWithdrawAmount(),
			 * TransactionStatus.WITHDRAW, bolenumFee); if (res.get() &&
			 * bolenumFee > 0) { transactionService.performEthTransaction(user,
			 * admin.getEthWalletaddress(), bolenumFee, TransactionStatus.FEE,
			 * null); } } break; default: return
			 * ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
			 * localService.getMessage("invalid.coin.code"), null); }
			 */
			break;

		case "ERC20TOKEN":
			validWithdrawAmount = btcWalletService.validateErc20WithdrawAmount(user, coinCode,
					withdrawBalanceForm.getWithdrawAmount());
			logger.debug("Validate balance: {}", validWithdrawAmount);
			if (validWithdrawAmount) {
				btcWalletService.withdrawAmount(currencyType, coinCode, user, withdrawBalanceForm.getToAddress(),
						withdrawBalanceForm.getWithdrawAmount(), bolenumFee, admin);
				/*
				 * Future<Boolean> res =
				 * transactionService.performErc20Transaction(user, coinCode,
				 * withdrawBalanceForm.getToAddress(),
				 * withdrawBalanceForm.getWithdrawAmount(),
				 * TransactionStatus.WITHDRAW, bolenumFee); if (res.get() &&
				 * bolenumFee > 0) {
				 * transactionService.performErc20Transaction(user, coinCode,
				 * admin.getEthWalletaddress(), bolenumFee,
				 * TransactionStatus.FEE, null); }
				 */
			}
			break;
		default:
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.coin.code"),
					null);
		}
		if (!validWithdrawAmount) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false,
					localService.getMessage("withdraw.invalid.amount"), null);

		}
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("withdraw.coin.success"), null);
	}

	/**
	 * 
	 * @param transaction
	 * @return
	 * 
	 */

	@RequestMapping(value = UrlConstant.DEPOSIT_TRANSACTION_STATUS, method = RequestMethod.POST)
	public ResponseEntity<Object> depositTransactionStatus(@RequestBody Transaction transaction) {
		Transaction transactionResponse = btcWalletService.setDepositeList(transaction);
		if (transactionResponse == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("Deposit not saved!"),
					null);
		} else {
			return ResponseHandler.response(HttpStatus.OK, false,
					localService.getMessage("Deposit saved successfully!"), transactionResponse);
		}
	}

}