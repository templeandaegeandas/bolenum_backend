/*@Description Of Class
 * 
 * OrderController class is responsible for below listed task: 
 * 
 *    Create Order
 *    Get traded orders
 *    Get Buy order list with pair
 *    Get sell order list with pair
 *    Get traded orders of logged in user
 *    Get order from user Book
 *    Get order details of user
 *    Cancel orders
 */

package com.bolenum.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.annotation.Secured;
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

	@Autowired
	private BankAccountDetailsService bankDetailsService;

	@Autowired
	private UserService userService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	
	
	
	
	/**@Description: use to create Order
	 * 
	 * @param orders
	 * 
	 * @return  order.processed.success OR order.volume.zero OR order.verify.kyc OR order.system.sync OR order.insufficient.balance
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.CREATE_ORDER, method = RequestMethod.POST)
	public ResponseEntity<Object> createOrder(@RequestBody Orders orders) {
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
		String balance = ordersService.checkOrderEligibility(user, orders);
		logger.debug("balance: {} of user: {}", balance, user.getEmailId());
		if (balance.equals("Synchronizing")) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.system.sync"), null);
		}
		if (!balance.equals("proceed")) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
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

	/**@Description use to get Buy order list with pair
	 * 
	 * @param marketCurrencyId
	 * @param pairedCurrencyId
	 * @return order list
	 */
	@RequestMapping(value = UrlConstant.BUY_ORDER_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getBuyOrderListWithPair(@RequestParam("marketCurrencyId") long marketCurrencyId,
			@RequestParam("pairedCurrencyId") long pairedCurrencyId) {
		Page<Orders> list = ordersService.getBuyOrdersListByPair(marketCurrencyId, pairedCurrencyId);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.list"), list);
	}
	
	/**@Description use to get sell order list with pair
	 * 
	 * @param marketCurrencyId
	 * @param pairedCurrencyId
	 * @return order list
	 */
	@RequestMapping(value = UrlConstant.SELL_ORDER_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getSellOrderListWithPair(@RequestParam("marketCurrencyId") long marketCurrencyId,
			@RequestParam("pairedCurrencyId") long pairedCurrencyId) {
		Page<Orders> list = ordersService.getSellOrdersListByPair(marketCurrencyId, pairedCurrencyId);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.list"), list);
	}
	
	/**@Description use to get traded orders of logged in user
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @param orderType
	 * @param date
	 * @return trade list
	 */

	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.TRADE_LIST_LOGGEDIN, method = RequestMethod.GET)
	public ResponseEntity<Object> getTradedOrdersLoggedInUser(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy, @RequestParam("orderType") String orderType,
			@RequestParam("date") Long date) {
		User user = GenericUtils.getLoggedInUser();
		Page<Trade> list = tradeService.getTradedOrdersLoggedIn(user, pageNumber, pageSize, sortOrder, sortBy,
				orderType, date);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("trade.list"), list);
	}
	
	
	/**@Description use to get traded orders 
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 *
	 * @return trade list
	 */

	@RequestMapping(value = UrlConstant.TRADE_LIST_ALL, method = RequestMethod.GET)
	public ResponseEntity<Object> getTradedOrders(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy) {
		Page<Trade> list = tradeService.getTradedOrders(pageNumber, pageSize, sortOrder, sortBy);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("trade.list"), list);
	}
	
	

	/**@Description use to get order from user Book
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 *
	 * @return order list
	 */

	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.MY_ORDER_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getMyOrdereFromBook(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy) {
		User user = GenericUtils.getLoggedInUser();
		Page<Orders> list = ordersService.findOrdersListByUserAndOrderStatus(pageNumber, pageSize, sortOrder, sortBy,
				user, OrderStatus.SUBMITTED);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.list"), list);
	}
	
	

	/**@Description use to get order details
	 * 
	 * @param orderId
	 * 
	 * @return bankAccountDetails,orderDetails
	 */

	@Secured("ROLE_USER")
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
		Map<String, Object> map = new HashMap<>();
		map.put("bankDetails", bankAccountDetails);
		map.put("orderDetails", orders);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("message.success"), map);
	}
	
	/**@Description use to cancel orders
	 * 
	 * @param orderId
	 * 
	 * @return order cancel OR order.cancel.error
	 */

	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.CANCEL_ORDER, method = RequestMethod.DELETE)
	public ResponseEntity<Object> cancelOrders(@RequestParam("orderId") long orderId) {
		Orders order = ordersService.findByOrderId(orderId);
		if (order == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("order.cancel.error"), Optional.empty());
		}
		if (!OrderStatus.SUBMITTED.equals(order.getOrderStatus())) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("order.execution.state"), Optional.empty());
		}
		ordersService.cancelOrder(order);
		simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_ORDER,
				com.bolenum.enums.MessageType.ORDER_BOOK_NOTIFICATION);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.cancel"),
				Optional.empty());
	}
}
