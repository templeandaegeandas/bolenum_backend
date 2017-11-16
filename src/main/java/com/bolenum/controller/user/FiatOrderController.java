/**
 * 
 */
package com.bolenum.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.enums.OrderType;
import com.bolenum.model.BankAccountDetails;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.services.common.BankAccountDetailsService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.order.book.FiatOrderService;
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
	private FiatOrderService fiatOrderService;
	
	@Autowired
	private OrdersService ordersService;

	@Autowired
	private BankAccountDetailsService bankAccountDetailsService;

	private Logger logger = LoggerFactory.getLogger(FiatOrderController.class);

	@RequestMapping(value = UrlConstant.CREATE_ORDER_FIAT, method = RequestMethod.POST)
	public ResponseEntity<Object> initializeOrder(@RequestParam("pairId") long pairId,
			@RequestParam("orderId") long matchedOrderId, @RequestBody Orders orders) {
		User user = GenericUtils.getLoggedInUser();
		logger.debug("matched order id: {}", matchedOrderId);
		Orders matchedOrder = ordersService.getOrderDetails(matchedOrderId);
		if (matchedOrder == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("invalid.order"),
					null);
		}
		if (matchedOrder.getVolume() <= 0) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("invalid.order"),
					null);
		}
		boolean kycVerified = userService.isKycVerified(user);
		if (!kycVerified) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.verify.kyc"),
					null);
		}
		String balance = fiatOrderService.checkFiatOrderEligibility(user, orders, pairId);
		if (balance.equals("Synchronizing")) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.system.sync"), null);
		}
		if (!balance.equals("proceed")) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("order.insufficient.balance"), null);
		}
		orders.setUser(user);
		Boolean result = fiatOrderService.processFiatOrderList(matchedOrder, orders, orders.getPair());
		if (result) {
			User bankDetails = null;
			if (orders.getOrderType().equals(OrderType.BUY)) {
				bankDetails = matchedOrder.getUser();
			} else {
				bankDetails = orders.getUser();
			}
			BankAccountDetails accountDetails = bankAccountDetailsService.primaryBankAccountDetails(bankDetails);
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.processed.success"),
					accountDetails);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("order.processed.fail"), null);
		}
	}
}
