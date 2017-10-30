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
import com.bolenum.enums.OrderStandard;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.order.book.OrdersService;
import com.bolenum.services.order.book.TradeService;
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
	private TradeService tradeService;

	@Autowired
	private LocaleService localeService;

	@RequestMapping(value = UrlConstant.CREATE_ORDER, method = RequestMethod.POST)
	public ResponseEntity<Object> createOrder(@RequestBody Orders orders) {
		// can not place order on 0 prize
		if (orders.getOrderStandard().equals(OrderStandard.LIMIT) && orders.getPrice() <= 0) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.price.zero"),
					null);
		}
		User user = GenericUtils.getLoggedInUser();
		boolean isVerified = user.getUserKyc().getIsVerified();
		if(!isVerified){
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.verify.kyc"), null);
		}
		String balance = ordersService.checkOrderEligibility(user, orders);
		logger.debug("balance: {}", balance);
		if (balance.equals("Synchronizing")) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.system.sync"), null);
		}
		if (!balance.equals("proceed")) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("order.insufficient.balance"), null);
		}
		orders.setUserId(user.getUserId());
		Boolean result = ordersService.processOrder(orders);
		if (result) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.processed.success"),
					null);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("order.processed.fail"), null);
		}
	}

	@RequestMapping(value = UrlConstant.BUY_ORDER_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getBuyOrderListWithPair(@RequestParam("pairId") Long pairId) {
		Page<Orders> list = ordersService.getBuyOrdersListByPair(pairId);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.list"), list);
	}

	@RequestMapping(value = UrlConstant.SELL_ORDER_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getSellOrderListWithPair(@RequestParam("pairId") Long pairId) {
		Page<Orders> list = ordersService.getSellOrdersListByPair(pairId);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.list"), list);
	}

	@RequestMapping(value = UrlConstant.TRADE_LIST_LOGGEDIN, method = RequestMethod.GET)
	public ResponseEntity<Object> getTradedOrdersLoggedInUser(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy) {
		User user = GenericUtils.getLoggedInUser();
		Page<Trade> list = tradeService.getTradedOrdersLoggedIn(user, pageNumber, pageSize, sortOrder, sortBy);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("trade.list"), list);
	}
	
	@RequestMapping(value = UrlConstant.TRADE_LIST_ALL, method = RequestMethod.GET)
	public ResponseEntity<Object> getTradedOrders(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy) {
		User user = GenericUtils.getLoggedInUser();
		Page<Trade> list = tradeService.getTradedOrders(user, pageNumber, pageSize, sortOrder, sortBy);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("trade.list"), list);
	}

}