package com.bolenum.controller.user;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.EditUserForm;
import com.bolenum.dto.common.PasswordForm;
import com.bolenum.dto.common.UserSignupForm;
import com.bolenum.enums.OrderType;
import com.bolenum.enums.TransactionStatus;
import com.bolenum.exceptions.InvalidOtpException;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.Countries;
import com.bolenum.model.States;
import com.bolenum.model.SubscribedUser;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.coin.UserCoin;
import com.bolenum.model.notification.Notification;
import com.bolenum.services.common.CountryAndStateService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.common.coin.Erc20TokenService;
import com.bolenum.services.order.book.MarketPriceService;
import com.bolenum.services.order.book.OrdersService;
import com.bolenum.services.user.AuthenticationTokenService;
import com.bolenum.services.user.SubscribedUserService;
import com.bolenum.services.user.UserService;
import com.bolenum.services.user.notification.NotificationService;
import com.bolenum.services.user.transactions.TransactionService;
import com.bolenum.services.user.wallet.BTCWalletService;
import com.bolenum.services.user.wallet.EtherumWalletService;
import com.bolenum.util.ErrorCollectionUtil;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * @Author Himanshu Kumar
 * @Date 11-Sep-2017
 */

@RestController
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
@Api(value = "User Controller")
public class UserController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private LocaleService localService;

	@Autowired
	private AuthenticationTokenService authenticationTokenService;

	@Autowired
	private BTCWalletService btcWalletService;

	@Autowired
	private EtherumWalletService etherumWalletService;

	@Autowired
	private Erc20TokenService erc20TokenService;

	@Autowired
	private CountryAndStateService countryAndStateService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private OrdersService orderService;

	@Autowired
	private SubscribedUserService subscribedUserService;

	@Autowired
	private MarketPriceService marketPriceService;

	@Autowired
	private NotificationService notificationService;

	/**
	 * 
	 * @param signupForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = UrlConstant.REGISTER_USER, method = RequestMethod.POST)
	public ResponseEntity<Object> registerUser(@Valid @RequestBody UserSignupForm signupForm, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(result),
					ErrorCollectionUtil.getErrorMap(result));
		} else {
			User isUserExist = userService.findByEmail(signupForm.getEmailId());
			if (isUserExist == null) {
				User newUser = signupForm.copy(new User());
				userService.registerUser(newUser);
				return ResponseHandler.response(HttpStatus.OK, false,
						localService.getMessage("user.registarion.success"), newUser.getEmailId());
			} else if (isUserExist.getIsEnabled()) {
				return ResponseHandler.response(HttpStatus.CONFLICT, false,
						localService.getMessage("email.already.exist"), isUserExist.getEmailId());
			} else {
				userService.reRegister(signupForm);
				return ResponseHandler.response(HttpStatus.OK, false,
						localService.getMessage("user.registarion.success"), signupForm.getEmailId());
			}
		}
	}

	/**
	 * 
	 * for mail verify at the time of sign up user as well as re register
	 * 
	 * @param token
	 * 
	 * @return
	 * 
	 * 
	 * @modified by Himanshu Kumar added expiry condition in reset password
	 * 
	 */
	@RequestMapping(value = UrlConstant.USER_MAIL_VERIFY, method = RequestMethod.GET)
	public ResponseEntity<Object> userMailVerify(@RequestParam("token") String token) {
		logger.debug("user mail verify token: {}", token);
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException(localService.getMessage("token.invalid"));
		}
		AuthenticationToken authenticationToken = authenticationTokenService.findByToken(token);
		if (authenticationToken == null) {
			logger.debug("user mail verify authenticationToken is null");
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false, localService.getMessage("token.invalid"),
					null);
		}

		boolean isExpired = authenticationTokenService.isTokenExpired(authenticationToken);
		logger.debug("user mail verify token expired: {}", isExpired);
		if (isExpired) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false, localService.getMessage("token.expired"),
					null);
		}

		User user = authenticationToken.getUser();
		if (user.getIsEnabled()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false,
					localService.getMessage("link.already.verified"), null);
		}
		erc20TokenService.createErc20Wallet(user, "BLN");
		etherumWalletService.createEthWallet(user, "ETH");
		String address = btcWalletService.createBtcAccount(String.valueOf(user.getUserId()));
		logger.debug("user mail verify wallet uuid: {}", address);
		if (!address.isEmpty()) {
			UserCoin userCoin = userService.saveUserCoin(address, user, "BTC");
			if (userCoin == null) {
				return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
						localService.getMessage("message.error"), Optional.empty());
			}
			user.setBtcWalletUuid(String.valueOf(user.getUserId()));
			user.setIsEnabled(true);
			List<UserCoin> userCoins = new ArrayList<>();
			userCoins.add(userCoin);
			user.setUserCoin(userCoins);
			User savedUser = userService.saveUser(user);
			logger.debug("user mail verify savedUser: {}", savedUser);
			if (savedUser != null) {
				return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), null);
			} else {
				return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
						localService.getMessage("message.error"), null);
			}
		} else {
			return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
					localService.getMessage("message.error"), null);
		}
	}

	/**
	 * for change password
	 * 
	 * @param passwordForm
	 * @param result
	 * @return
	 */

	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.CHANGE_PASS, method = RequestMethod.PUT)
	public ResponseEntity<Object> changePassword(@Valid @RequestBody PasswordForm passwordForm, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(result),
					ErrorCollectionUtil.getErrorMap(result));
		} else {
			User user = GenericUtils.getLoggedInUser();
			boolean response;
			try {
				response = userService.changePassword(user, passwordForm);
			} catch (InvalidPasswordException e) {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("invalid.request"), null);
			}
			if (response) {
				return ResponseHandler.response(HttpStatus.OK, false,
						localService.getMessage("user.password.change.success"), null);
			} else {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("user.password.change.failure"), null);
			}
		}
	}

	/**
	 * used to update user profile
	 * 
	 * @param editUserForm
	 * @param result
	 * @return
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.UPDATE_USER_PROFILE, method = RequestMethod.PUT)
	public ResponseEntity<Object> updateUserProfile(@Valid @RequestBody EditUserForm editUserForm,
			BindingResult result) {
		User user = GenericUtils.getLoggedInUser();
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(result),
					ErrorCollectionUtil.getErrorMap(result));
		} else {
			User response = userService.updateUserProfile(editUserForm, user);
			if (response != null) {
				return ResponseHandler.response(HttpStatus.OK, false,
						localService.getMessage("user.profile.update.success"), response);
			} else {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("user.profile.update.failure"), null);
			}
		}
	}

	/**
	 * used to find out current logged in user
	 * 
	 * @return
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.GET_LOGGEDIN_USER, method = RequestMethod.GET)
	public ResponseEntity<Object> getLoggedinUser() {
		User user = GenericUtils.getLoggedInUser();
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), user);
	}

	/**
	 * 
	 * used to add mobile number at the time of providing profile information by
	 * user
	 * 
	 * @param mobileNumber
	 * @param countryCode
	 * @return
	 * @throws PersistenceException
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.ADD_MOBILE_NUMBER, method = RequestMethod.PUT)
	public ResponseEntity<Object> addMobileNumber(@RequestParam("mobileNumber") String mobileNumber,
			@RequestParam("countryCode") String countryCode) throws PersistenceException {
		User user = GenericUtils.getLoggedInUser();
		User response = userService.addMobileNumber(mobileNumber, countryCode, user);
		if (response != null) {
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("otp.sent.success"),
					response);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("otp.sent.failure"),
					null);
		}
	}

	/**
	 * used for verification of OTP
	 * 
	 * @param otp
	 * @return
	 * @throws PersistenceException
	 * @throws InvalidOtpException
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.VERIFY_OTP, method = RequestMethod.PUT)
	public ResponseEntity<Object> verify(@RequestParam("otp") Integer otp) {
		User user = GenericUtils.getLoggedInUser();
		Boolean response;
		try {
			response = userService.verifyOTP(otp, user);
			if (response) {
				return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("otp.verified"), null);
			}
		} catch (InvalidOtpException e) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("otp.not.verified"),
					Optional.empty());
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("otp.not.verified"),
				Optional.empty());
	}

	/**
	 * 
	 * used to authenticate user via OTP
	 * 
	 * @return
	 * @throws PersistenceException
	 * @throws InvalidOtpException
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.RESEND_OTP, method = RequestMethod.POST)
	public ResponseEntity<Object> resendOtp() {
		User user = GenericUtils.getLoggedInUser();
		userService.resendOTP(user);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("otp.resend"), null);
	}

	/**
	 * to upload user profile image
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws PersistenceException
	 * @throws MaxSizeExceedException
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.UPLOAD_PROFILE_IMAGE, method = RequestMethod.POST)
	public ResponseEntity<Object> uploadKycDocument(@RequestParam String profilePic)
			throws IOException, PersistenceException, MaxSizeExceedException {
		User user = GenericUtils.getLoggedInUser();
		User response = userService.uploadImage(profilePic, user.getUserId());
		if (response != null) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localService.getMessage("user.image.uploaded.success"), response);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localService.getMessage("user.image.uploaded.failed"), null);
		}
	}

	/**
	 * to get list of countries
	 * 
	 * @return
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.GET_COUNTRIES_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getCountriesList() {
		List<Countries> list = countryAndStateService.getCountriesList();
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("all.countries.list"), list);
	}

	/**
	 * 
	 * to get list of states with respect to specific country
	 * 
	 * @param countryId
	 * @return
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.GET_STATE_BY_COUNTRY_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getStatesByCountryId(@RequestParam("countryId") Long countryId) {
		List<States> list = countryAndStateService.getStatesByCountry(countryId);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("states.list.by.country.id"),
				list);
	}

	/**
	 * 
	 * used to get list of transaction done by a particular user, user can only see
	 * his own transactions at the time of deposited to his wallet
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @return
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.TRANSACTION_LIST_OF_USER_WITHDRAW, method = RequestMethod.GET)
	public ResponseEntity<Object> getWithdrawTransactionList(@RequestParam("currencyName") String currencyName,
			@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize,
			@RequestParam("sortOrder") String sortOrder, @RequestParam("sortBy") String sortBy) {
		User user = GenericUtils.getLoggedInUser();
		Page<Transaction> listOfUserTransaction = transactionService.getListOfUserTransaction(currencyName, user,
				TransactionStatus.WITHDRAW, pageNumber, pageSize, sortOrder, sortBy);
		return ResponseHandler.response(HttpStatus.OK, false,
				localService.getMessage("transaction.list.withdraw.success"), listOfUserTransaction);
	}

	/**
	 * used to get list of transaction done by a particular user, user can only see
	 * his own transactions at the time of deposited to his wallet
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @return
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.TRANSACTION_LIST_OF_USER_DEPOSIT, method = RequestMethod.GET)
	public ResponseEntity<Object> getDepositTransactionList(@RequestParam("currencyName") String currencyName,
			@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize,
			@RequestParam("sortOrder") String sortOrder, @RequestParam("sortBy") String sortBy) {
		User user = GenericUtils.getLoggedInUser();
		Page<Transaction> listOfUserTransaction = transactionService.getListOfUserTransaction(currencyName, user,
				TransactionStatus.DEPOSIT, pageNumber, pageSize, sortOrder, sortBy);
		return ResponseHandler.response(HttpStatus.OK, false,
				localService.getMessage("transaction.list.deposit.success"), listOfUserTransaction);
	}

	/**
	 * to get number of trading buy/sell performed by particular user
	 * 
	 * @return
	 */

	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.MY_TRADING_COUNT, method = RequestMethod.GET)
	public ResponseEntity<Object> getUserTradingCount() {
		User user = GenericUtils.getLoggedInUser();
		Long totalNumberOfBuy = orderService.countOrdersByOrderTypeAndUser(user, OrderType.BUY);
		Long totalNumberOfSell = orderService.countOrdersByOrderTypeAndUser(user, OrderType.SELL);
		Long totalNumberOfTrading = totalNumberOfBuy + totalNumberOfSell;
		Map<String, Long> tradingNumber = new HashMap<>();
		tradingNumber.put("totalNumberOfBuy", totalNumberOfBuy);
		tradingNumber.put("totalNumberOfSell", totalNumberOfSell);
		tradingNumber.put("totalNumberOfTrading", totalNumberOfTrading);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("my.trading.count.success"),
				tradingNumber);
	}

	/**
	 * 
	 * @param email
	 * @return
	 */
	@RequestMapping(value = UrlConstant.SUBSCRIBE_USER, method = RequestMethod.POST)
	public ResponseEntity<Object> sendNewsLetter(@RequestParam("email") String email) {

		if (!GenericUtils.isValidMail(email)) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localService.getMessage("user.email.not.valid"), null);
		}
		SubscribedUser subscribedUser = subscribedUserService.validateSubscribedUser(email);
		if (subscribedUser == null) {
			SubscribedUser response = subscribedUserService.saveSubscribedUser(email);
			if (response != null) {
				return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("user.subscribe.success"),
						response);
			} else {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("user.subscribe.failure"), null);
			}
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
				localService.getMessage("user.subscribe.already.exist"), null);
	}

	/**
	 * to get number of trading buy/sell performed by particular user
	 * 
	 * @return
	 */

	@RequestMapping(value = UrlConstant.COIN_MARKET_DATA, method = RequestMethod.GET)
	public ResponseEntity<Object> getCoinMarketData(@RequestParam("marketCurrencyId") long marketCurrency,
			@RequestParam("pairedCurrencyId") long pairedCurrency) {
		try {
			Double volume24h = marketPriceService.ordersIn24hVolume(marketCurrency, pairedCurrency);
			Double high24h = marketPriceService.ordersIn24hHigh(marketCurrency, pairedCurrency);
			long countTrade24h = marketPriceService.tradesIn24h(marketCurrency, pairedCurrency);
			Double low24h = marketPriceService.ordersIn24hLow(marketCurrency, pairedCurrency);
			Map<String, Object> map = new HashMap<>();
			map.put("volume24h", volume24h);
			map.put("high24h", high24h);
			map.put("low24h", low24h);
			map.put("countTrade24h", countTrade24h);
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("coin.market.data.success"),
					map);
		} catch (ParseException e) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localService.getMessage("coin.market.data.failure"), Optional.empty());
		}
	}

	/**
	 * 
	 * @return
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.USER_NOTIFICATION, method = RequestMethod.GET)
	public ResponseEntity<Object> getNotificationList(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy) {
		User user = GenericUtils.getLoggedInUser();
		Page<Notification> listOfUserNotification = notificationService.getListOfNotification(user, pageNumber,
				pageSize, sortOrder, sortBy);
		return ResponseHandler.response(HttpStatus.OK, true, localService.getMessage("message.success"),
				listOfUserNotification);

	}

	/**
	 * 
	 * @param id
	 * 
	 * @return
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.USER_NOTIFICATION, method = RequestMethod.POST)
	public ResponseEntity<Object> setActionOnNotificton(@RequestParam("id") Long id) {
		Notification notification = notificationService.getRequestedNotification(id);
		notification = notificationService.setActionOnNotifiction(notification);
		return ResponseHandler.response(HttpStatus.OK, true, localService.getMessage("message.success"), notification);

	}

	/**
	 * 
	 * @return
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.COUNT_USER_NOTIFICATION, method = RequestMethod.GET)
	public ResponseEntity<Object> countUserNotification() {
		User user = GenericUtils.getLoggedInUser();
		Long totalUnseenNotification = notificationService.countUnSeenNotification(user);
		return ResponseHandler.response(HttpStatus.OK, true, localService.getMessage("message.success"),
				totalUnseenNotification);

	}

}
