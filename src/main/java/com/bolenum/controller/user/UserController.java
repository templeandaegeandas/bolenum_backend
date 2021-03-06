/*@Description Of Class
 * 
 * UserControler class is responsible for below listed task: 
 * 
 *    Register user details in database.
 *    Verifies user e-mail.
 *    Change user password.
 *    Update user profile & upload images.
 *    Use to upload user KYC document.
 *    Find out current logged in user.
 *    Add mobile number at the time of providing profile information by user.
 *    Verification of OTP.
 *    Get list of all countries/states with respect to specific country.
 *    Used to get list of transaction done by a particular user.
 *    Get number of trading buy/sell performed by user.
 *    Use to get coin market data.
 *    Get notifications of user.
 *    Send news latter to the user.
 *    Change notification status on notification list of user.
 *    Get total count of user's notification.
 */
package com.bolenum.controller.user;

import java.io.IOException;
import java.text.ParseException;
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
 * 
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

	private static final String MESSAGE_SUCCESS = "message.success";
	
	
	/**@Description: RegisterUser method is use to register the new user.(SignUp process).
	 *               Checks the existing email-id of new user.
	 * 
	 * @param signupForm
	 * 
	 * @return  registration successfully OR email already exist.
	 */
	
	@RequestMapping(value = UrlConstant.REGISTER_USER, method = RequestMethod.POST)
	public ResponseEntity<Object> registerUser(@Valid @RequestBody UserSignupForm signupForm, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(result),
					ErrorCollectionUtil.getErrorMap(result));
		}
		User isUserExist = userService.findByEmail(signupForm.getEmailId());
		if (isUserExist == null) {
			User newUser = signupForm.copy(new User());
			userService.registerUser(newUser);
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("user.registarion.success"),
					newUser.getEmailId());
		} else if (isUserExist.getIsEnabled()) {
			return ResponseHandler.response(HttpStatus.CONFLICT, false, localService.getMessage("email.already.exist"),
					isUserExist.getEmailId());
		} else {
			userService.reRegister(signupForm);
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("user.registarion.success"),
					signupForm.getEmailId());
		}

	}

	/**
	 * 
	 * @Description : Verifies mail at the time of user sign up  as well as when user re-register
	 * 
	 * @param token
	 * 
	 * @return token invalid OR token expired OR Mail verified.
	 
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
			User savedUser = userService.saveUser(user);
			logger.debug("user mail verify savedUser: {}", savedUser);
			if (savedUser != null) {
				return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage(MESSAGE_SUCCESS), null);
			}
		}
		return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
				localService.getMessage("message.error"), null);
	}

	/**
	 * @Description: For user password change.
	 * 
	 * @param passwordForm
	 * 
	 * @return password changed OR password change failure.
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
	 * @Description : Use to update user profile(SignUp details).
	 * 
	 * @param editd User Form
	 * 
	 * @return profile updated successfully OR user profile updated failure
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
	 * @Description : Use to find out current logged in user
	 * 
	 * @return: current logged in user
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.GET_LOGGEDIN_USER, method = RequestMethod.GET)
	public ResponseEntity<Object> getLoggedinUser() {
		User user = GenericUtils.getLoggedInUser();
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage(MESSAGE_SUCCESS), user);
	}

	/**@Descripton: Use to add mobile number at the time of providing profile information by user.
	 * 
     * @param mobileNumber
	 * @param countryCode
	 * @throws PersistenceException
	 * @return OTP
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

	/**@Description : use for verification of OTP
	 * 
	 * @param OTP
	 * 
	 * @throws PersistenceException
	 * @throws InvalidOtpException
	 * @return OTP verified OR OTP not verified
	 * 
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

	/**@Description : use to resend OTP
	 * 
	 * @throws PersistenceException
	 * @throws InvalidOtpException
	 * @return OTP
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.RESEND_OTP, method = RequestMethod.POST)
	public ResponseEntity<Object> resendOtp() {
		User user = GenericUtils.getLoggedInUser();
		userService.resendOTP(user);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("otp.resend"), null);
	}

	/**@Description : Use to upload user KYC document
	 * 
	 * 
	 * @param file
	 * @throws IOException
	 * @throws PersistenceException
	 * @throws MaxSizeExceedException
	 * @return image uploaded OR image uploaded failed
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

	/**@Description : Use to get list of countries.
	 * 
	 * 
	 * @return: Countries list
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.GET_COUNTRIES_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getCountriesList() {
		List<Countries> list = countryAndStateService.getCountriesList();
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("all.countries.list"), list);
	}

	/**@Description : Use to get list of states with respect to specific country.
	 * 
	 * @param: countryId
	 * @return: states list by country id
	 */
	@Secured("ROLE_USER")
	@RequestMapping(value = UrlConstant.GET_STATE_BY_COUNTRY_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getStatesByCountryId(@RequestParam("countryId") Long countryId) {
		List<States> list = countryAndStateService.getStatesByCountry(countryId);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("states.list.by.country.id"),
				list);
	}

	/**@Description : use to get list of Withdraw transaction done by a particular user, user can only see
	 *                his own transactions at the time of deposited to his wallet
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @return List withdraw Transaction of user.
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

	/**@Description : use to get list of deposit transaction done by a particular user, user can only see
	 *                his own transactions at the time of deposited to his wallet.
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @return List deposit Transaction of user.
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

	/**@description : Use to get number of trading buy/sell performed by particular user.
	 * 
	 * 
	 * @return totalNumberOfBuy OR totalNumberOfSell OR totalNumberOfTrading
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

	/**@description : Use to send news latter to the user.
	 *  
	 *  
	 * @param email
	 * @return invalid email OR news latter sent.
	 */
	@RequestMapping(value = UrlConstant.SUBSCRIBE_USER, method = RequestMethod.POST)
	public ResponseEntity<Object> sendNewsLetter(@RequestParam("email") String email) {

		if (!GenericUtils.isValidMail(email)) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localService.getMessage("user.email.not.valid"), Optional.empty());
		}
		SubscribedUser subscribedUser = subscribedUserService.validateSubscribedUser(email);
		if (subscribedUser == null) {
			 subscribedUserService.saveSubscribedUser(email);
		}
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("user.subscribe.success"),
				Optional.empty());
	}

	/**@Description : Use to get coin market data.
	 * 
	 * @param  : marketCurrencyId  
	 * @param  : pairedCurrencyId
	 * @return : coin market data OR volume24h,high24h,low24h,countTrade24h
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

	/** @Description: Get notification list of user
	 *
	 * @param: pageNumber
	 * @param: pageSize
	 * @param: sortOrder
	 * @param: sortBy
	 * @return list Of user notifications.
	 */
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@RequestMapping(value = UrlConstant.USER_NOTIFICATION, method = RequestMethod.GET)
	public ResponseEntity<Object> getNotificationList(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy) {
		User user = GenericUtils.getLoggedInUser();
		Page<Notification> listOfUserNotification = notificationService.getListOfNotification(user, pageNumber,
				pageSize, sortOrder, sortBy);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage(MESSAGE_SUCCESS),
				listOfUserNotification);

	}

	/**@Description: Use to change notification status to read.
	 *               
	 * 
	 * @created by Himanshu Kumar
	 * 
	 * @param arrayOfNotification
	 * 
	 * @return  Notification list
	 */
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@RequestMapping(value = UrlConstant.USER_NOTIFICATION, method = RequestMethod.PUT)
	public ResponseEntity<Object> setActionOnNotificton(
			@RequestParam("arrayOfNotification") Long[] arrayOfNotification) {
		notificationService.changeNotificationsStatus(arrayOfNotification);

		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage(MESSAGE_SUCCESS),
				arrayOfNotification);
	}

	/**@Description: get total count of user's notification.
	 * 
	 * @created by Himanshu Kumar
	 * 
	 * @return Unseen notifications.
	 * 
	 */
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@RequestMapping(value = UrlConstant.COUNT_USER_NOTIFICATION, method = RequestMethod.GET)
	public ResponseEntity<Object> countUserNotification() {
		User user = GenericUtils.getLoggedInUser();
		Long totalUnseenNotification = notificationService.countUnSeenNotification(user);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage(MESSAGE_SUCCESS),
				totalUnseenNotification);
	}

}
