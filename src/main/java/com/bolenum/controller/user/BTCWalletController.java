/*@Description Of Class
 * 
 * BTCWalletController class is responsible for below listed task: 
 *     
 *     Get wallet address and balance
 *     Get btc to eth price
 *     Get withdraw amount
 *     Save deposit Transaction
 *     Create Account.
 *     
 */
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.WithdrawBalanceForm;
import com.bolenum.enums.CurrencyType;
import com.bolenum.exceptions.InsufficientBalanceException;
import com.bolenum.model.Currency;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.coin.Erc20Token;
import com.bolenum.model.coin.UserCoin;
import com.bolenum.model.fees.WithdrawalFee;
import com.bolenum.repo.common.coin.UserCoinRepository;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.admin.fees.WithdrawalFeeService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.common.coin.Erc20TokenService;
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

	@Autowired
	private UserCoinRepository userCoinRepository;

	@Value("${admin.email}")
	private String adminEmail;

	private Logger logger = LoggerFactory.getLogger(BTCWalletController.class);

	/**@Description use to get the wallet address and QR code for get deposited in the wallet 
	 * @param currencyType
	 * @param coinCode
	 * @exception IllegalArgumentException
	 * @return MAP<ADDRESS ,BALANCE> 
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.DEPOSIT, method = RequestMethod.GET)
	public ResponseEntity<Object> getWalletAddressAndBalance(@RequestParam(name = "currencyType") String currencyType,
			@RequestParam(name = "code") String coinCode) {
		logger.debug("currency Type: {}, code:{}", currencyType, coinCode);
		if (coinCode == null || coinCode.isEmpty()) {
			throw new IllegalArgumentException(localService.getMessage("invalid.coin.code"));
		}
		User user = GenericUtils.getLoggedInUser(); // logged in user
		Map<String, Object> map = new HashMap<>();
		final String ADDRESS = "address";
		final String BALANCE = "balance";
		switch (currencyType) {
		case "CRYPTO":
			switch (coinCode) {
			case "BTC":
				Map<String, Object> mapAddressAndBal = new HashMap<>();
				mapAddressAndBal.put(ADDRESS, btcWalletService.getBtcAccountAddress(user.getBtcWalletUuid()));
				mapAddressAndBal.put(BALANCE, btcWalletService.getBtcAccountBalance(user.getBtcWalletUuid()));
				map.put("data", mapAddressAndBal);
				break;
			case "ETH":
				UserCoin userCoin = etherumWalletService.ethWalletBalance(user, coinCode);
				if (userCoin == null) {
					return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
							localService.getMessage("There is an error for getting balance of user for: " + coinCode),
							null);
				}
				Map<String, Object> mapAddress = new HashMap<>();
				mapAddress.put(ADDRESS, userCoin.getWalletAddress());
				mapAddress.put(BALANCE, userCoin.getBalance());
				map.put("data", mapAddress);
				break;
			default:
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("invalid.coin.code"), null);
			}
			break;
		case "ERC20TOKEN":
			Erc20Token erc20Token = erc20TokenService.getByCoin(coinCode);
			UserCoin userCoin = erc20TokenService.erc20WalletBalance(user, erc20Token);
			if (userCoin == null) {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("There is an error for getting balance of user for: " + coinCode),
						null);
			}
			Map<String, Object> mapAddress = new HashMap<>();
			mapAddress.put(ADDRESS, userCoin.getWalletAddress());
			mapAddress.put(BALANCE, GenericUtils.getDecimalFormat(userCoin.getBalance()));
			map.put("data", mapAddress);
			break;
		case "FIAT":
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), null);
		default:
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.coin.code"),
					null);
		}
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), map);
	}

	/**@description Use to get btc to Eth price
	 * 
	 * @param currencyAbbreviation
	 * @return marketPrice
	 */
	@RequestMapping(value = UrlConstant.MARKET_PRICE, method = RequestMethod.GET)
	public ResponseEntity<Object> getBtcToEthPrice(@RequestParam("symbol") String currencyAbbreviation) {
		Currency marketPrice = currencyService.findByCurrencyAbbreviation(currencyAbbreviation);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), marketPrice);
	}

	/**@description use to get withdraw amount
	 * @param currencyType
	 * @param withdrawBalanceForm
	 * @param coinCode
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws InsufficientBalanceException
	 * @return withdraw coin success
	 * 
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.WITHDRAW, method = RequestMethod.POST)
	public ResponseEntity<Object> withdrawAmount(@RequestParam(name = "currencyType") String currencyType,
			@Valid @RequestBody WithdrawBalanceForm withdrawBalanceForm, @RequestParam(name = "code") String coinCode,
			BindingResult bindingResult) {
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
					withdrawBalanceForm.getWithdrawAmount(), withdrawalFee, currency,
					withdrawBalanceForm.getToAddress());
			logger.debug("Validate balance: {}", validWithdrawAmount);
			if (validWithdrawAmount) {
				btcWalletService.withdrawAmount(currencyType, coinCode, user, withdrawBalanceForm.getToAddress(),
						withdrawBalanceForm.getWithdrawAmount(), bolenumFee, admin);
			}
			break;

		case "ERC20TOKEN":
			validWithdrawAmount = btcWalletService.validateErc20WithdrawAmount(user, coinCode,
					withdrawBalanceForm.getWithdrawAmount(), withdrawalFee, withdrawBalanceForm.getToAddress());
			logger.debug("Validate balance: {}", validWithdrawAmount);
			if (validWithdrawAmount) {
				btcWalletService.withdrawAmount(currencyType, coinCode, user, withdrawBalanceForm.getToAddress(),
						withdrawBalanceForm.getWithdrawAmount(), bolenumFee, admin);
			}
			break;
		default:
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.coin.code"),
					Optional.empty());
		}
		if (!validWithdrawAmount) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false,
					localService.getMessage("withdraw.invalid.amount"), Optional.empty());

		}
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("withdraw.coin.success"),
				Optional.empty());
	}

	/**@description Use to save deposit Transaction 
	 * 
	 * @param transaction
	 * @return transactionResponse
	 * 
	 */
	@RequestMapping(value = UrlConstant.DEPOSIT_TRANSACTION_STATUS, method = RequestMethod.POST)
	public ResponseEntity<Object> depositTransactionStatus(@RequestBody Transaction transaction) {
		Transaction transactionResponse = btcWalletService.setDepositeList(transaction);
		if (transactionResponse == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("Deposit not saved!"),
					Optional.empty());
		} else {
			return ResponseHandler.response(HttpStatus.OK, false,
					localService.getMessage("Deposit saved successfully!"), transactionResponse);
		}
	}

	/**@description Use to createAccount.
	 * 
	 * @param currencyAbbreviation
	 * @return message.success OR message.error
	 */
	@RequestMapping(value = UrlConstant.CREATE_ACCOUNT, method = RequestMethod.POST)
	public ResponseEntity<Object> createAccount(@RequestParam("uuid") long uuid) {
		User user = userService.findByUserId(uuid);
		if (user != null) {
			String address = btcWalletService.createBtcAccount(String.valueOf(uuid));
			logger.debug("address: {}", address);
			UserCoin userCoin = userCoinRepository.findByTokenNameAndUser("BTC", user);
			if (userCoin == null) {
				userCoin = new UserCoin();
				userCoin.setTokenName("BTC");
				userCoin.setWalletAddress(address);
				userCoin.setCurrencyType(CurrencyType.CRYPTO);
				userCoin.setUser(user);
				userCoin = userCoinRepository.save(userCoin);
				userService.saveUser(user);
				return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"),
						Optional.empty());
			}
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("message.error"),
				Optional.empty());
	}

}