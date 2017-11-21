/**
 * 
 */
package com.bolenum.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.bolenum.dto.common.AddUserBankDetailsForm;
import com.bolenum.enums.CurrencyType;
import com.bolenum.enums.MessageType;
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

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	private Logger logger = LoggerFactory.getLogger(FiatOrderController.class);

	@RequestMapping(value = UrlConstant.CREATE_ORDER_FIAT, method = RequestMethod.POST)
	public ResponseEntity<Object> createFiateOrder(@RequestBody Orders orders) {
		Page<Orders> page = fiatOrderService.existingOrders(orders, 0, 10);
		if (page.getTotalElements() > 0) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.exist.fiat"),
					page);
		}
		orders = fiatOrderService.createOrders(orders);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.create.success"),
				orders.getId());
	}

	@RequestMapping(value = UrlConstant.CREATE_ORDER_FIAT, method = RequestMethod.PUT)
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

		if (!bankAccountDetailsService.isBankAccountAdded(user)) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("bank.details.not.exist"), null);
		}

		String balance = fiatOrderService.checkFiatOrderEligibility(user, orders, pairId);
		if (balance.equals("Synchronizing")) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.system.sync"), null);
		}
		if (!balance.equals("proceed")) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("order.insufficient.balance"), null);
		}
		if (!(orders.getPair().getToCurrency().get(0).getCurrencyType().equals(CurrencyType.FIAT)
				|| orders.getPair().getPairedCurrency().get(0).getCurrencyType().equals(CurrencyType.FIAT))) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.not.fiat"),
					null);
		}
		List<Orders> list = new ArrayList<>();
		list.add(matchedOrder);
		orders.setUser(user);
		if (ordersService.isUsersSelfOrder(orders, list)) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.self.fail"),
					null);
		}
		Orders order = fiatOrderService.processFiatOrderList(matchedOrder, orders, orders.getPair());
		if (order.getId() != null) {
			User bankDetailsUser = null;
			Map<String, Object> map = new HashMap<>();
			if (orders.getOrderType().equals(OrderType.BUY)) {
				bankDetailsUser = matchedOrder.getUser();
				BankAccountDetails accountDetails = bankAccountDetailsService
						.primaryBankAccountDetails(bankDetailsUser);
				map.put("accountDetails", response(accountDetails));
				map.put("orderId", order.getId());

			} else {
				bankDetailsUser = orders.getUser();
				BankAccountDetails accountDetails = bankAccountDetailsService
						.primaryBankAccountDetails(bankDetailsUser);
				String msg = "Hi " + matchedOrder.getUser().getFirstName()
						+ ", Your order's seller bank details: Account holder name:"
						+ accountDetails.getAccountHolderName() + " Account Number: "
						+ accountDetails.getAccountNumber() + " BVN: " + accountDetails.getIfscCode()
						+ " Please login to bolenum exchange to confirm your payment.";
				notificationService.sendNotification(matchedOrder.getUser(), msg);
				notificationService.saveNotification(bankDetailsUser, matchedOrder.getUser(), msg);
				map.put("orderId", order.getId());
				simpMessagingTemplate.convertAndSend(
						UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_ORDER_SELLER_CONFIRM,
						MessageType.ORDER_CONFIRMATION + "#" + matchedOrder.getId());
			}
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.processed.success"),
					map);

		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("order.processed.fail"), null);
		}
	}

	private AddUserBankDetailsForm response(BankAccountDetails bank) {
		AddUserBankDetailsForm form = new AddUserBankDetailsForm();
		form.setAccountHolderName(bank.getAccountHolderName());
		form.setAccountNumber(bank.getAccountNumber());
		form.setIfscCode(bank.getIfscCode());
		form.setBankName(bank.getBankName());
		return form;
	}

	/**
	 * buyer has paid the amount to seller and sending confirmation
	 * 
	 * @param orderId
	 *            of buyer
	 *
	 */
	@RequestMapping(value = UrlConstant.ORDER_FIAT_PAID, method = RequestMethod.PUT)
	public ResponseEntity<Object> confirmFiatPaidOrder(@RequestParam("orderId") long orderId) {
		Orders exitingOrder = ordersService.getOrderDetails(orderId);
		if (exitingOrder == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("invalid.order"),
					null);
		}
		boolean result = fiatOrderService.buyerPaidConfirmtion(exitingOrder);
		if (result) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.seller.notified"),
					null);
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("invalid.order"), null);
	}

	@RequestMapping(value = UrlConstant.ORDER_FIAT_CANCEL, method = RequestMethod.PUT)
	public ResponseEntity<Object> cancelOrder(@RequestParam("orderId") long orderId) {
		Orders exitingOrder = ordersService.getOrderDetails(orderId);
		if (exitingOrder == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("invalid.order"),
					null);
		}
		boolean result = fiatOrderService.processCancelOrder(exitingOrder);
		if (result) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.cancel"), null);
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("order.cancel.error"),
				null);
	}

	/**
	 * 
	 * this process confirmation of Seller
	 * 
	 * @param orderId
	 *            of seller
	 *
	 */
	@RequestMapping(value = UrlConstant.ORDER_FIAT_TX, method = RequestMethod.PUT)
	public ResponseEntity<Object> processTransactionFiatOrders(@RequestParam("orderId") long orderId) {
		Orders exitingOrder = ordersService.getOrderDetails(orderId);
		if (exitingOrder == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("invalid.order"),
					null);
		}
		boolean result = fiatOrderService.processTransactionFiatOrders(exitingOrder);
		if (result) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("order.transaction.success"),
					null);
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("invalid.order"), null);
	}
}
