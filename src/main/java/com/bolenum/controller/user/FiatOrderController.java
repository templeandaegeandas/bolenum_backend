/**
 * 
 */
package com.bolenum.controller.user;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.order.book.OrdersService;
import com.bolenum.services.user.UserService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * @author chandan kumar singh
 * @date 15-Nov-2017
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
@Api(value = "Fiat Order Controller")
public class FiatOrderController {

	@Autowired
	private UserService userService;

	@Autowired
	private LocaleService localeService;
	
	@Autowired
	private OrdersService ordersService;

	@RequestMapping(value = UrlConstant.CREATE_ORDER_FIAT, method = RequestMethod.POST)
	public ResponseEntity<Object> createOrder(@RequestParam("pairId") long pairId, @RequestBody Orders orders) {
		User user = GenericUtils.getLoggedInUser();
		boolean kycVerified = userService.isKycVerified(user);
		if (!kycVerified) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.verify.kyc"),
					null);
		}
		String balance = ordersService.checkFiatOrderEligibility(user, orders, pairId);
		if (balance.equals("Synchronizing")) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.system.sync"), null);
		}
		if (!balance.equals("proceed")) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("order.insufficient.balance"), null);
		}
		orders.setUser(user);
		Boolean result = null;
		try {
			result = ordersService.processOrder(orders);
		} catch (InterruptedException | ExecutionException e) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage(e.getMessage()),
					null);
		}
		if (result) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.processed.success"),
					null);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.self.fail"),
					null);
		}
	}
}
