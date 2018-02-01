/**Description Of Class
 * 
 *     FiatOrderController class is responsible for below listed task:
 *     
 *     Create a Fiat order
 *     Initialize order
 *     Add user bank details
 *     Confirm Fiat Paid Order
 *     Use to cancel order
 *     Confirmation of Seller transaction of fiat orders
 *     Get order by order id
 *     Get list of orders against type of Orders(SELL/BUY)
 *     
 */
package com.bolenum.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.EmailTemplate;
import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.AddUserBankDetailsForm;
import com.bolenum.dto.orders.OrdersDTO;
import com.bolenum.enums.CurrencyType;
import com.bolenum.enums.MessageType;
import com.bolenum.enums.NotificationType;
import com.bolenum.enums.OrderType;
import com.bolenum.model.BankAccountDetails;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.services.common.BankAccountDetailsService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.order.book.FiatOrderService;
import com.bolenum.services.order.book.OrdersService;
import com.bolenum.services.user.UserService;
import com.bolenum.services.user.notification.NotificationService;
import com.bolenum.util.ErrorCollectionUtil;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * @author chandan kumar singh
 * @modified Vishal Kumar
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

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	private Logger logger = LoggerFactory.getLogger(FiatOrderController.class);

	private static final String ORDERID = "orderId";

	private static final String INVALIDORDER = "invalid.order";

	
	 /**@Description to create a Fiat order, if order based on volume and price does not match any
	 * existing order then only it will be saved. Otherwise existing order list will
	 * be returned
	 * 
	 * @param Order
	 * @return list of order/ created order id
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.CREATE_ORDER_FIAT, method = RequestMethod.POST)
	public ResponseEntity<Object> createFiateOrder(@RequestBody Orders orders) {
		User user = GenericUtils.getLoggedInUser();
		boolean kycVerified = userService.isKycVerified(user);
		if (!kycVerified) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.verify.kyc"),
					Optional.empty());
		}

		if (!bankAccountDetailsService.isBankAccountAdded(user)) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("bank.details.not.exist"), Optional.empty());
		}
		Page<Orders> page = fiatOrderService.existingOrders(orders, 0, 10, orders.getMarketCurrency().getCurrencyId(),
				orders.getPairedCurrency().getCurrencyId());
		if (page.getTotalElements() > 0) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.exist.fiat"),
					page);
		}
		if (OrderType.SELL.equals(orders.getOrderType())) {
			String balance = fiatOrderService.checkFiatOrderEligibility(user, orders);
			if (!balance.equals("proceed")) {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localeService.getMessage("order.insufficient.balance"), Optional.empty());
			}
		}
		orders.setUser(user);
		orders = fiatOrderService.createOrders(orders);
		simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_ORDER,
				com.bolenum.enums.MessageType.ORDER_BOOK_NOTIFICATION);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.create.success"),
				orders.getId());
	}

	// @changed by vishal kumar: unrelated response removed
	/**@Description Use to initialize order
	 * 
	 * @param orderId
	 * @param orders
	 * @param BindingResult
	 * @return order.processed.success OR order.processed.failure 
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.CREATE_ORDER_FIAT, method = RequestMethod.PUT)
	public ResponseEntity<Object> initializeOrder(@RequestParam("orderId") long matchedOrderId,
			@Valid @RequestBody OrdersDTO ordersDTO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(bindingResult),
					ErrorCollectionUtil.getErrorMap(bindingResult));
		}
		Orders orders = ordersDTO.copy(new Orders());
		User user = GenericUtils.getLoggedInUser();
		logger.debug("matched order id: {}", matchedOrderId);
		Orders matchedOrder = ordersService.getOrderDetails(matchedOrderId);
		if (matchedOrder == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage(INVALIDORDER),
					Optional.empty());
		}
		if (matchedOrder.getVolume() <= 0) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage(INVALIDORDER),
					Optional.empty());
		}
		boolean kycVerified = userService.isKycVerified(user);
		if (!kycVerified) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.verify.kyc"),
					Optional.empty());
		}

		if (!bankAccountDetailsService.isBankAccountAdded(user)) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("bank.details.not.exist"), Optional.empty());
		}

		String balance = fiatOrderService.checkFiatOrderEligibility(user, orders);
		if (OrderType.SELL.equals(orders.getOrderType()) && !balance.equals("proceed")) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false,
					localeService.getMessage("order.insufficient.balance"), Optional.empty());
		}
		boolean toType = CurrencyType.FIAT.equals(orders.getMarketCurrency().getCurrencyType());
		boolean pairType = CurrencyType.FIAT.equals(orders.getPairedCurrency().getCurrencyType());
		if (!(toType || pairType)) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.not.fiat"),
					Optional.empty());
		}
		List<Orders> list = new ArrayList<>();
		list.add(matchedOrder);
		orders.setUser(user);
		if (ordersService.isUsersSelfOrder(orders, list)) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.self.fail"),
					Optional.empty());
		}
		Orders order = fiatOrderService.processFiatOrderList(matchedOrder, orders);
		if (order == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("order.processed.fail"), Optional.empty());
		}
		Map<String, Object> map = new HashMap<>();
		if (OrderType.BUY.equals(orders.getOrderType())) {
			map.put(ORDERID, order.getId());
		} else {
			Map<String, Object> infoMap = new HashMap<>();
			User bankDetailsUser = orders.getUser();
			BankAccountDetails accountDetails = bankAccountDetailsService.primaryBankAccountDetails(bankDetailsUser);
			String msg = "Hi " + matchedOrder.getUser().getFirstName()
					+ ", Your order's seller bank details: Account holder name:" + accountDetails.getAccountHolderName()
					+ " Account Number: " + accountDetails.getAccountNumber()
					+ " Please login to bolenum exchange to confirm your payment.";

			User buyer = matchedOrder.getUser();
			infoMap.put("buyerName", buyer.getFullName());
			infoMap.put("buyerEmailId", buyer.getEmailId());
			infoMap.put("sellerAccountName", accountDetails.getAccountHolderName());
			infoMap.put("sellerAccountNumber", accountDetails.getAccountNumber());

			notificationService.sendNotification(matchedOrder.getUser(), "trade.summary", infoMap,
					EmailTemplate.SELLER_ACCOUNT_DETAILS);
			notificationService.saveNotification(bankDetailsUser, matchedOrder.getUser(), msg, matchedOrderId,
					NotificationType.MATCHED_NOTIFICATION);
			map.put(ORDERID, order.getId());
			simpMessagingTemplate.convertAndSend(
					UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + matchedOrder.getUser().getUserId(),
					MessageType.MATCHED_NOTIFICATION);
		}
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.processed.success"), map);
	}
	/**@Description Use to add user bank details form
	 * 
	 * @param BankAccountDetails
	 *
	 * @return Form 
	 */
	private AddUserBankDetailsForm response(BankAccountDetails bank) {
		AddUserBankDetailsForm form = new AddUserBankDetailsForm();
		form.setAccountHolderName(bank.getAccountHolderName());
		form.setAccountNumber(bank.getAccountNumber());
		form.setBankName(bank.getBankName());
		return form;
	}

	/**
	 *@Description buyer has paid the amount to seller and sending confirmation
	 * 
	 *@param orderId
	 *           
	 *@return order.seller.notified
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.ORDER_FIAT_PAID, method = RequestMethod.PUT)
	public ResponseEntity<Object> confirmFiatPaidOrder(@RequestParam("orderId") long orderId) {
		Orders exitingOrder = ordersService.getOrderDetails(orderId);
		if (exitingOrder == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage(INVALIDORDER),
					Optional.empty());
		}
		boolean result = fiatOrderService.buyerPaidConfirmtion(exitingOrder);
		if (result) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.seller.notified"),
					Optional.empty());
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage(INVALIDORDER),
				Optional.empty());
	}
	
	 /**@Description use to cancel order
	 * 
	 *@param orderId
	 *           
	 *@return order cancel
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.ORDER_FIAT_CANCEL, method = RequestMethod.PUT)
	public ResponseEntity<Object> cancelOrder(@RequestParam("orderId") long orderId) {
		Orders exitingOrder = ordersService.getOrderDetails(orderId);
		if (exitingOrder == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage(INVALIDORDER),
					Optional.empty());
		}
		if (exitingOrder.getMatchedOrder() != null && exitingOrder.getMatchedOrder().getUser() != null) {
			if (exitingOrder.getMatchedOrder().isConfirm()) {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localeService.getMessage("Balance already paid to you. So you can't perform this action!"),
						Optional.empty());
			}
			simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/"
					+ exitingOrder.getMatchedOrder().getUser().getUserId(), MessageType.ORDER_CANCELLED);
		}
		boolean result = fiatOrderService.processCancelOrder(exitingOrder);
		if (result) {
			simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_ORDER,
					com.bolenum.enums.MessageType.ORDER_BOOK_NOTIFICATION);
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.cancel"),
					Optional.empty());
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.cancel.error"),
				Optional.empty());
	}

	/**@Description this process confirmation of Seller transaction of fiat orders
	 * @param orderId  of seller
	 *@return order.transaction.success
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.ORDER_FIAT_TX, method = RequestMethod.PUT)
	public ResponseEntity<Object> processTransactionFiatOrders(@RequestParam("orderId") long orderId) {
		Orders exitingOrder = ordersService.getOrderDetails(orderId);
		if (exitingOrder == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage(INVALIDORDER),
					Optional.empty());
		}
		if (!exitingOrder.getMatchedOrder().isConfirm()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.not.confirm"),
					Optional.empty());
		}
		logger.debug("existing order id: {}", exitingOrder.getId());
		String currencyAbr;
		String currencyType;
		if (!(CurrencyType.FIAT.equals(exitingOrder.getMarketCurrency().getCurrencyType()))) {
			currencyAbr = exitingOrder.getMarketCurrency().getCurrencyAbbreviation();
			currencyType = exitingOrder.getMarketCurrency().getCurrencyType().toString();
		} else {
			currencyAbr = exitingOrder.getPairedCurrency().getCurrencyAbbreviation();
			currencyType = exitingOrder.getPairedCurrency().getCurrencyType().toString();
		}
		logger.debug("existing order currency Abr: {}", currencyAbr);
		Future<Boolean> result = fiatOrderService.processTransactionFiatOrders(exitingOrder, currencyAbr, currencyType);
		logger.debug("existing order currency Abr: {}", currencyAbr);
		try {
			logger.debug("result of order: {}", result);
			if (result.get()) {
				simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/"
						+ exitingOrder.getMatchedOrder().getUser().getUserId(), MessageType.PAID_NOTIFICATION);
				return ResponseHandler.response(HttpStatus.OK, false,
						localeService.getMessage("order.transaction.success"), Optional.empty());
			}
		} catch (InterruptedException | ExecutionException e) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage(INVALIDORDER),
					Optional.empty());
		}
		logger.debug("response: {}", result);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage(INVALIDORDER),
				Optional.empty());
	}

	/**
	 * @Description get order by order id
	 * @param orderId
	 * @param orderType
	 * @return single order
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.ORDER_FIAT_BY_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getOrders(@RequestParam("orderId") long orderId,
			@RequestParam("orderType") OrderType orderType) {
		Map<String, Object> map = new HashMap<>();
		Orders orders = ordersService.getOrderDetails(orderId);
		if (orders != null) {
			if (OrderType.BUY.equals(orderType)) {
				BankAccountDetails accountDetails = bankAccountDetailsService
						.primaryBankAccountDetails(orders.getMatchedOrder().getUser());
				Map<String, String> userAddress = fiatOrderService.byersWalletAddressAndCurrencyAbbr(
						orders.getMatchedOrder().getUser(), orders.getMarketCurrency(), orders.getPairedCurrency());
				map.put("accountDetails", response(accountDetails));
				map.put(ORDERID, orders.getId());
				map.put("createdDate", orders.getCreatedOn());
				map.put("totalPrice", orders.getLockedVolume() * orders.getPrice());
				map.put("sellerName", orders.getMatchedOrder().getUser().getFirstName());
				map.put("orderVolume", orders.getLockedVolume());
				map.put("walletAddress", userAddress.get("address"));
				map.put("currencyAbr", userAddress.get("currencyAbbr"));
				map.put("orderStatus", orders.getOrderStatus());
				map.put("matchedOn", orders.getMatchedOn());
				map.put("isConfirmed", orders.isConfirm());
			} else {
				Map<String, String> userAddress = fiatOrderService.byersWalletAddressAndCurrencyAbbr(
						orders.getMatchedOrder().getUser(), orders.getMatchedOrder().getMarketCurrency(),
						orders.getMatchedOrder().getPairedCurrency());
				BankAccountDetails accountDetails = bankAccountDetailsService
						.primaryBankAccountDetails(orders.getUser());
				map.put("accountDetails", response(accountDetails));
				map.put(ORDERID, orders.getId());
				map.put("createdDate", orders.getCreatedOn());
				map.put("totalPrice", orders.getLockedVolume() * orders.getPrice());
				map.put("sellerName", orders.getUser().getFirstName());
				map.put("orderVolume", orders.getLockedVolume());
				map.put("walletAddress", userAddress.get("address"));
				map.put("currencyAbr", userAddress.get("currencyAbbr"));
				map.put("orderStatus", orders.getOrderStatus());
				map.put("matchedOn", orders.getMatchedOn());
				map.put("isConfirmed", orders.isConfirm());
				map.put("isDispute", orders.isDispute());
				if (orders.getMatchedOrder() != null) {
					map.put("isMatchedConfirm", orders.getMatchedOrder().isConfirm());
				}
			}
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("message.success"), map);
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, false, localeService.getMessage("order.create.success"),
				Optional.empty());
	}

	/**@Description get list of orders against type of Orders(SELL/BUY), If requested order is
	 * BUY then it will return SELL order list, and for SELL order return BUY order
	 * list
	 * @param order
	 * @return list or orders
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.ORDER_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getOrdersList(@RequestParam("volume") double volume,
			@RequestParam("price") double price, @RequestParam("orderType") OrderType orderType,
			@RequestParam("marketCurrencyId") long marketCurrencyId,
			@RequestParam("pairedCurrencyId") long pairedCurrencyId) {
		Orders orders = new Orders();
		orders.setVolume(volume);
		orders.setPrice(price);
		orders.setOrderType(orderType);
		Page<Orders> page = fiatOrderService.existingOrders(orders, 0, Integer.MAX_VALUE, marketCurrencyId,
				pairedCurrencyId);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("message.success"), page);
	}

}