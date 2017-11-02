
package com.bolenum.controller.user;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.WithdrawBalanceForm;
import com.bolenum.model.TransactionFee;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.MarketPrice;
import com.bolenum.services.admin.Erc20TokenService;
import com.bolenum.services.admin.TransactionFeeService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.order.book.MarketPriceService;
import com.bolenum.services.user.transactions.TransactionService;
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
	private MarketPriceService marketPriceService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private TransactionFeeService transactionFeeService;

	/**
	 * to get the wallet address and QR code for get deposited in the
	 * wallet @description getWalletAddressAndQrCode @param coin code @return
	 * ResponseEntity<Map<String,Object>> @exception
	 *
	 */
	@RequestMapping(value = UrlConstant.DEPOSIT, method = RequestMethod.GET)
	public ResponseEntity<Object> getWalletAddressAndQrCode(@RequestParam(name = "currencyType") String currencyType,
			@RequestParam(name = "code") String coinCode) {
		if (coinCode == null || coinCode.isEmpty()) {
			throw new IllegalArgumentException(localService.getMessage("invalid.coin.code"));
		}

		User user = GenericUtils.getLoggedInUser(); // logged in user
		Map<String, Object> map = new HashMap<>();
		switch (currencyType) {
		case "CRYPTO":
			switch (coinCode) {
			case "BTC":
				map = btcWalletService.getWalletAddressAndQrCode(user.getBtcWalletUuid());
				break;
			case "ETH":
				Double balance = etherumWalletService.getWalletBalance(user);
				Map<String, Object> mapAddress = new HashMap<>();
				mapAddress.put("address", user.getEthWalletaddress());
				mapAddress.put("balance", balance + " ETH");
				map.put("data", mapAddress);
				break;
			default:
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("invalid.coin.code"), null);
			}
			break;
		case "ERC20TOKEN":
			try {
				Double balance = erc20TokenService.getErc20WalletBalance(user, coinCode);
				Map<String, Object> mapAddress = new HashMap<>();
				mapAddress.put("address", user.getEthWalletaddress());
				mapAddress.put("balance", balance + " BLN");
				map.put("data", mapAddress);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException | IOException | CipherException | InterruptedException
					| ExecutionException e) {
				e.printStackTrace();
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("invalid.coin.code"), null);
			}
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
		MarketPrice marketPrice = marketPriceService.findByCurrencyId(currencyAbbreviation);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), marketPrice);
	}

	/**
	 * 
	 * @param currencyType
	 * @param withdrawBalanceForm
	 * @param coinCode
	 * @return
	 */
	@RequestMapping(value = UrlConstant.WITHDRAW, method = RequestMethod.POST)
	public ResponseEntity<Object> withdrawAmountFromWallet(@RequestParam(name = "currencyType") String currencyType,
			@Valid @RequestBody WithdrawBalanceForm withdrawBalanceForm, @RequestParam(name = "code") String coinCode,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localService.getMessage("withdraw.invalid.amount"), null);
		}
		if (coinCode == null || coinCode.isEmpty()) {
			throw new IllegalArgumentException(localService.getMessage("invalid.coin.code"));
		}
		TransactionFee transactionFee = transactionFeeService.getTransactionFeeDetails();

		User user = GenericUtils.getLoggedInUser(); // logged in user
		boolean validAvailableWalletBalance = false;
		boolean validWithdrawAmount = false;

		switch (currencyType) {
		case "CRYPTO":
			switch (coinCode) {
			case "BTC":
				String balanceBTC = btcWalletService.getWalletBalnce(user.getBtcWalletUuid());
				balanceBTC = balanceBTC.replace("BTC", "");
				balanceBTC = balanceBTC.trim();
				Double availableBTCBalance = Double.valueOf(balanceBTC);
				validAvailableWalletBalance = validateAvailableWalletBalance(availableBTCBalance,
						transactionFee.getAvailableBalanceLimitToWithdrawForBTC());
				validWithdrawAmount = validateWithdrawAmount(availableBTCBalance,
						withdrawBalanceForm.getWithdrawAmount());
				if (validAvailableWalletBalance && validWithdrawAmount) {
					transactionService.performBtcTransaction(user, withdrawBalanceForm.getToAddress(),
							withdrawBalanceForm.getWithdrawAmount());
				}
				break;
			case "ETH":
				Double availableETHBalance = etherumWalletService.getWalletBalance(user);
				validAvailableWalletBalance = validateAvailableWalletBalance(availableETHBalance,
						transactionFee.getAvailableBalanceLimitToWithdrawForETH());
				validWithdrawAmount = validateWithdrawAmount(availableETHBalance,
						withdrawBalanceForm.getWithdrawAmount());
				if (validAvailableWalletBalance && validWithdrawAmount) {
					transactionService.performEthTransaction(user, withdrawBalanceForm.getToAddress(),
							withdrawBalanceForm.getWithdrawAmount());
				}
				break;
			default:
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("invalid.coin.code"), null);
			}
			break;
		case "ERC20TOKEN":
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.coin.code"),
					null);
		case "FIAT":
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.coin.code"),
					null);
		default:
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.coin.code"),
					null);
		}

		if (!validAvailableWalletBalance) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false,
					localService.getMessage("withdraw.invalid.available.balance"), null);
		}

		if (!validWithdrawAmount) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false,
					localService.getMessage("withdraw.invalid.amount"), null);
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, false, localService.getMessage("Withdraw successfully"),
				null);
	}

	private boolean validateWithdrawAmount(Double availableBalance, Double withdrawAmount) {
		if (availableBalance >= withdrawAmount) {
			return true;
		}
		return false;
	}

	private boolean validateAvailableWalletBalance(Double availableBalance,
			Double availableBalanceLimitToWithdrawForBTC) {
		if (availableBalance >= availableBalanceLimitToWithdrawForBTC) {
			return true;
		}
		return false;
	}
}