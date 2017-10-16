package com.bolenum.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.enums.OrderType;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.order.book.OrdersService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * 
 * @author Vishal kumar
 * @date 10-Oct-2017
 *
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
@Api(value = "Order Controller")
public class OrderController {
	private Logger logger = LoggerFactory.getLogger(OrderController.class);
	@Autowired
	private OrdersService ordersService;

	@Autowired
	private LocaleService localeService;

	@RequestMapping(value = UrlConstant.CREATE_ORDER, method = RequestMethod.POST)
	public ResponseEntity<Object> createOrder(@RequestBody Orders orders) {
		User user = GenericUtils.getLoggedInUser();
		String balance = ordersService.checkOrderEligibility(user, orders);
		logger.debug("balance: {}", balance);
		if (balance.equals("Synchronizing")) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.system.sync"), null);
		}
		if (!balance.equals("proceed")) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("order.insufficient.balance"), null);
		}
		Boolean result = ordersService.processOrder(orders);
		if (result) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.processed.success"),
					null);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("order.processed.fail"), null);
		}
	}

	@RequestMapping(value = UrlConstant.LIST_ORDER, method = RequestMethod.GET)
	public ResponseEntity<Object> getOrderListWithPair(@RequestParam("pairId") Long pairId,
			@RequestParam("orderType") OrderType orderType) {
		Page<Orders> list = ordersService.getOrdersListByPair(pairId, orderType);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.list"), list);
	}

}
