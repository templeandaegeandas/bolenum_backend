package com.bolenum.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.enums.OrderStatus;
import com.bolenum.model.BankAccountDetails;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.services.common.BankAccountDetailsService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.order.book.OrdersService;
import com.bolenum.services.order.book.TradeService;
import com.bolenum.services.user.UserService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
@Scope("prototype")
public class OrderController {
	private Logger logger = LoggerFactory.getLogger(OrderController.class);
	@Autowired
	private OrdersService ordersService;

	@Autowired
	private TradeService tradeService;

	@Autowired
	private LocaleService localeService;

	@Autowired
	private BankAccountDetailsService bankDetailsService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@RequestMapping(value = UrlConstant.CREATE_ORDER, method = RequestMethod.POST)
	public ResponseEntity<Object> createOrder(@RequestParam("pairId") long pairId, @RequestBody Orders orders) {
		if (orders.getVolume() * orders.getPrice() < 0.0001) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.volume.zero"),
					null);
		}
		User user = GenericUtils.getLoggedInUser();
		boolean kycVerified = userService.isKycVerified(user);
		if (!kycVerified) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.verify.kyc"),
					null);
		}
		String balance = ordersService.checkOrderEligibility(user, orders, pairId);
		logger.debug("balance: {}", balance);
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

	/**
	 * 
	 * @param pairId
	 * @return
	 */
	@RequestMapping(value = UrlConstant.BUY_ORDER_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getBuyOrderListWithPair(@RequestParam("pairId") Long pairId) {
		Page<Orders> list = ordersService.getBuyOrdersListByPair(pairId);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String stringList = mapper.writeValueAsString(list);
			logger.debug("Buy list as String: {}", stringList);
		} catch (JsonProcessingException e) {
			logger.debug("error in buy get list: {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.list"), list);
	}

	@RequestMapping(value = UrlConstant.SELL_ORDER_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getSellOrderListWithPair(@RequestParam("pairId") Long pairId) {
		Page<Orders> list = ordersService.getSellOrdersListByPair(pairId);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String stringList = mapper.writeValueAsString(list);
			logger.debug("Sell list as String: {}", stringList);
		} catch (JsonProcessingException e) {
			logger.debug("error in sell get list: {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.list"), list);
	}

	@RequestMapping(value = UrlConstant.TRADE_LIST_LOGGEDIN, method = RequestMethod.GET)
	public ResponseEntity<Object> getTradedOrdersLoggedInUser(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy, @RequestParam("orderType") String orderType,
			@RequestParam("date") Long date) {
		User user = GenericUtils.getLoggedInUser();
		Page<Trade> list = tradeService.getTradedOrdersLoggedIn(user, pageNumber, pageSize, sortOrder, sortBy,
				orderType, date);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String stringList = mapper.writeValueAsString(list);
			logger.debug("logged in user trade list as String: {}", stringList);
		} catch (JsonProcessingException e) {
			logger.debug("error in logged in user trade get list: {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("trade.list"), list);
	}

	@RequestMapping(value = UrlConstant.TRADE_LIST_ALL, method = RequestMethod.GET)
	public ResponseEntity<Object> getTradedOrders(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy) {
		Page<Trade> list = tradeService.getTradedOrders(pageNumber, pageSize, sortOrder, sortBy);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String stringList = mapper.writeValueAsString(list);
			logger.debug("all trade list as String: {}", stringList);
		} catch (JsonProcessingException e) {
			logger.debug("error in all trade get list: {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("trade.list"), list);
	}

	@RequestMapping(value = UrlConstant.MY_ORDER_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getMyOrdereFromBook(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy) {
		User user = GenericUtils.getLoggedInUser();
		Page<Orders> list = ordersService.findOrdersListByUserAndOrderStatus(pageNumber, pageSize, sortOrder, sortBy,
				user, OrderStatus.SUBMITTED);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String stringList = mapper.writeValueAsString(list);
			logger.debug("my order as String: {}", stringList);
		} catch (JsonProcessingException e) {
			logger.debug("error in my order get list: {}", e.getMessage());
			e.printStackTrace();
		}
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.list"), list);
	}

	@RequestMapping(value = UrlConstant.ORDER_BY_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getOrderDetails(@RequestParam("orderId") long orderId) {
		User user = GenericUtils.getLoggedInUser();
		List<BankAccountDetails> banks = bankDetailsService.findByUser(user);
		BankAccountDetails bankAccountDetails = null;
		for (BankAccountDetails bank : banks) {
			if (bank.isPrimary()) {
				bankAccountDetails = bank;
				break;
			}
		}
		if (bankAccountDetails == null) {
			bankAccountDetails = banks.get(0);
		}
		Orders orders = ordersService.getOrderDetails(orderId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bankDetails", bankAccountDetails);
		map.put("orderDetails", orders);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("message.success"), map);
	}
	
	@RequestMapping(value = UrlConstant.CANCEL_ORDER, method = RequestMethod.DELETE)
	public ResponseEntity<Object> cancelOrders(@RequestParam("orderId") long orderId) {
		boolean status = ordersService.cancelOrder(orderId);
		if (status) {
			simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_ORDER,
					com.bolenum.enums.MessageType.ORDER_BOOK_NOTIFICATION);
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.cancel"), Optional.empty());
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.cancel.error"), Optional.empty());
	}
}
