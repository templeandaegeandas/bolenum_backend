/**
 * 
 */
package com.bolenum.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.model.User;
import com.bolenum.services.common.LocaleService;
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
	/**
	 * to get the wallet address and QR code for get deposited in the
	 * wallet @description getWalletAddressAndQrCode @param coin code @return
	 * ResponseEntity<Map<String,Object>> @exception
	 *
	 */
	@RequestMapping(value = UrlConstant.DEPOSIT, method = RequestMethod.GET)
	public ResponseEntity<Object> getWalletAddressAndQrCode(@RequestParam(name = "code") String coinCode) {
		if (coinCode == null || coinCode.isEmpty()) {
			throw new IllegalArgumentException(localService.getMessage("invalid.coin.code"));
		}
		User user= GenericUtils.getLoggedInUser(); // logged in user
		Map<String,Object> map = new HashMap<>();
		switch (coinCode) {
		case "BTC":
			map = btcWalletService.getWalletAddressAndQrCode(user.getBtcWalletUuid());
			break;
		case "ETH":
			Double balance = etherumWalletService.getWalletBalance(user);
			Map<String,Object> mapAddress = new HashMap<>();
			mapAddress.put("address", user.getEthWalletaddress());
			mapAddress.put("balance", balance +" ETH");
			map.put("data", mapAddress);
			break;
		default:
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.coin.code"),null);
		}
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"),map);
	}
}