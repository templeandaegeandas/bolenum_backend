package com.bolenum.controller.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.WithdrawBalanceForm;
import com.bolenum.enums.OrderType;
import com.bolenum.model.SubscribedUser;
import com.bolenum.model.User;
import com.bolenum.model.erc20token.Erc20Token;
import com.bolenum.model.fees.TradingFee;
import com.bolenum.model.fees.WithdrawalFee;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.services.admin.AdminService;
import com.bolenum.services.admin.fees.TradingFeeService;
import com.bolenum.services.admin.fees.WithdrawalFeeService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.common.erc20token.Erc20TokenService;
import com.bolenum.services.order.book.OrdersService;
import com.bolenum.services.user.AuthenticationTokenService;
import com.bolenum.services.user.SubscribedUserService;
import com.bolenum.services.user.wallet.BTCWalletService;
import com.bolenum.services.user.wallet.EtherumWalletService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * @Author Himanshu Kumar
 *
 * @Date 05-Sep-2017
 * @modified chandan kumar singh
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_ADMIN_URI_V1)
@Api(value = "Admin Controller")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private LocaleService localeService;

	@Autowired
	private TradingFeeService tradingFeeService;

	@Autowired
	private OrdersService ordersService;

	@Autowired
	private AuthenticationTokenService authenticationTokenService;

	@Autowired
	private WithdrawalFeeService withdrawalFeeService;

	@Autowired
	private SubscribedUserService subscribedUserService;
	
	@Autowired
	private BTCWalletService btcWalletService;
	
	@Autowired
	private EtherumWalletService etherumWalletService;
	
	@Autowired
	private Erc20TokenService erc20TokenService;

	public static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@RequestMapping()
	public ResponseEntity<Object> index() {
		return null;
	}

	/**
	 * to get list of all the users enrolled in system
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @param searchData
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.LIST_USERS, method = RequestMethod.GET)
	public ResponseEntity<Object> getUsersList(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortBy") String sortBy,
			@RequestParam("sortOrder") String sortOrder, @RequestParam("searchData") String searchData) {
		User user = GenericUtils.getLoggedInUser();
		Page<User> userList = adminService.getUsersList(pageNumber, pageSize, sortBy, sortOrder, searchData, user);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("admin.user.list"), userList);
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.GET_USER_BY_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getUsersById(@PathVariable("userId") Long userId) {
		User user = adminService.getUserById(userId);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("admin.user.get.by.id"), user);
	}

	/**
	 * to add trading fees for transaction done by user and deducted fees will be
	 * store in Admin wallet
	 * 
	 * @param tradingFee
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.TRADING_FEES, method = RequestMethod.POST)
	public ResponseEntity<Object> addTradingFees(@RequestBody TradingFee tradingFee) {
		TradingFee savedTradingFee = tradingFeeService.saveTradingFee(tradingFee);
		if (savedTradingFee != null) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("tradefee.success"),
					savedTradingFee);
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("tradefee.error"),
				Optional.empty());
	}

	/**
	 * to get the fee details of system
	 *
	 * @return transaction fee
	 */
	@RequestMapping(value = UrlConstant.TRADING_FEES, method = RequestMethod.GET)
	public ResponseEntity<Object> getTradingFees() {
		TradingFee fee = tradingFeeService.getTradingFee();
		return ResponseHandler.response(HttpStatus.OK, true,
				localeService.getMessage("admin.transaction.fees.found.success"), fee);
	}

	/**
	 * 
	 * @param withdrawalFee
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.WITHDRAWAL_FEES, method = RequestMethod.POST)
	public ResponseEntity<Object> saveWithdrawlFees(@RequestBody WithdrawalFee withdrawalFee) {
		WithdrawalFee savedWithdrawalFee = withdrawalFeeService.saveWithdrawalFee(withdrawalFee);
		if (savedWithdrawalFee != null) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("withdrawfee.success"),
					savedWithdrawalFee);
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("withdrawfee.error"),
				Optional.empty());
	}

	/**
	 * 
	 * @param currencyId
	 * @return
	 */
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@RequestMapping(value = UrlConstant.WITHDRAWAL_FEES, method = RequestMethod.GET)
	public ResponseEntity<Object> getWithdrawlFees(@RequestParam("currencyId") long currencyId) {
		WithdrawalFee fee = withdrawalFeeService.getWithdrawalFee(currencyId);
		return ResponseHandler.response(HttpStatus.OK, true, localeService.getMessage("message.success"), fee);
	}

	/**
	 * to count number of new buyers/sellers and active users and active orders that
	 * will be shown on Admin dashboard
	 * 
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.COUNT_BUYER_SELLER_DASHBOARD, method = RequestMethod.GET)
	public ResponseEntity<Object> getTotalOfBuyerAndSeller() {

		Long newBuyers = ordersService.getTotalCountOfNewerBuyerAndSeller(OrderType.BUY);
		Long newSellers = ordersService.getTotalCountOfNewerBuyerAndSeller(OrderType.SELL);
		Long activeUsers = authenticationTokenService.countActiveUsers();
		Long activeOrders = ordersService.countActiveOpenOrder();
		Map<String, Long> countOfusers = new HashMap<>();
		countOfusers.put("newBuyers", newBuyers);
		countOfusers.put("newSellers", newSellers);
		countOfusers.put("activeUsers", activeUsers);
		countOfusers.put("activeOrders", activeOrders);
		return ResponseHandler.response(HttpStatus.OK, true,
				localeService.getMessage("admin.count.user.dashboard.success"), countOfusers);
	}

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.DISPLAY_LATEST_ORDER, method = RequestMethod.GET)
	public ResponseEntity<Object> getLatestOrderList(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortBy") String sortBy,
			@RequestParam("sortOrder") String sortOrder) {

		Page<Orders> listOfLatestOrders = ordersService.getListOfLatestOrders(pageNumber, pageSize, sortBy, sortOrder);
		return ResponseHandler.response(HttpStatus.OK, true,
				localeService.getMessage("admin.latest.orders.list.success"), listOfLatestOrders);
	}

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.SUBSCRIBE_USER, method = RequestMethod.GET)
	public ResponseEntity<Object> getListOfSubscribedUser(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortBy") String sortBy,
			@RequestParam("sortOrder") String sortOrder) {

		Page<SubscribedUser> listOfSubscribedUser = subscribedUserService.getSubscribedUserList(pageNumber, pageSize,
				sortBy, sortOrder);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("admin.subscribed.user.list"),
				listOfSubscribedUser);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.DEPOSIT, method = RequestMethod.GET)
	public ResponseEntity<Object> getWalletAddressAndBalance(@RequestParam(name = "currencyType") String currencyType,
			@RequestParam(name = "code") String coinCode) {
		logger.debug("currency Type: {}, code:{}", currencyType, coinCode);
		if (coinCode == null || coinCode.isEmpty()) {
			throw new IllegalArgumentException(localeService.getMessage("invalid.coin.code"));
		}
		User user = GenericUtils.getLoggedInUser(); // logged in user
		Map<String, Object> map = new HashMap<>();
		switch (currencyType) {
		case "CRYPTO":

			switch (coinCode) {
			case "BTC":
				Map<String, Object> mapAddressAndBal = new HashMap<>();
				mapAddressAndBal.put("address", btcWalletService.getWalletAddress(user.getBtcWalletUuid()));
				mapAddressAndBal.put("balance", btcWalletService.getWalletBalance(user.getBtcWalletUuid()));
				map.put("data", mapAddressAndBal);
				break;
			case "ETH":
				Double balance = etherumWalletService.getWalletBalance(user);
				Map<String, Object> mapAddress = new HashMap<>();
				mapAddress.put("address", user.getEthWalletaddress());
				mapAddress.put("balance", balance);
				map.put("data", mapAddress);
				break;
			default:
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localeService.getMessage("invalid.coin.code"), null);
			}
			break;
		case "ERC20TOKEN":
			Erc20Token erc20Token = erc20TokenService.getByCoin(coinCode);
			Double balance = erc20TokenService.getAdminErc20WalletBalance(user, erc20Token);
			Map<String, Object> mapAddress = new HashMap<>();
			mapAddress.put("address", user.getEthWalletaddress());
			mapAddress.put("balance", GenericUtils.getDecimalFormat(balance));
			map.put("data", mapAddress);
			break;
		case "FIAT":
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("message.success"), null);
		default:
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("invalid.coin.code"),
					null);
		}
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("message.success"), map);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.ADMIN_WITHDRAW, method = RequestMethod.POST)
	public ResponseEntity<Object> withdrawAmount(@RequestParam(name = "currencyType") String currencyType,
			@Valid @RequestBody WithdrawBalanceForm withdrawBalanceForm, @RequestParam(name = "code") String coinCode,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("withdraw.invalid.amount"), Optional.empty());
		}
		if (coinCode == null || coinCode.isEmpty()) {
			throw new IllegalArgumentException(localeService.getMessage("invalid.coin.code"));
		}
		User user = GenericUtils.getLoggedInUser(); // logged in user
		/**
		 * getting currency minimum withdraw amount
		 */
		boolean validWithdrawAmount = false;
		switch (currencyType) {
		case "CRYPTO":
			validWithdrawAmount = adminService.adminValidateCryptoWithdrawAmount(user, coinCode,
					withdrawBalanceForm.getWithdrawAmount(), withdrawBalanceForm.getToAddress());
			logger.debug("Validate balance: {}", validWithdrawAmount);
			if (validWithdrawAmount) {
				adminService.adminWithdrawCryptoAmount(user, coinCode, withdrawBalanceForm.getWithdrawAmount(),
						withdrawBalanceForm.getToAddress());
			}
			break;
		case "ERC20TOKEN":
			validWithdrawAmount = adminService.adminValidateErc20WithdrawAmount(user, coinCode,
					withdrawBalanceForm.getWithdrawAmount(), withdrawBalanceForm.getToAddress());
			logger.debug("Validate balance: {}", validWithdrawAmount);
			if (validWithdrawAmount) {
				adminService.adminWithdrawErc20TokenAmount(user, coinCode, withdrawBalanceForm.getWithdrawAmount(),
						withdrawBalanceForm.getToAddress());
			}
			break;
		default:
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("invalid.coin.code"),
					Optional.empty());
		}
		if (!validWithdrawAmount) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false,
					localeService.getMessage("withdraw.invalid.amount"), Optional.empty());

		}
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("withdraw.coin.success"),
				Optional.empty());
	}

}