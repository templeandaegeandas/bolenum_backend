package com.bolenum.controller.user;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.bolenum.enums.TransactionStatus;
import com.bolenum.exceptions.InvalidOtpException;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.Countries;
import com.bolenum.model.States;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.services.common.CountryAndStateService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.user.AuthenticationTokenService;
import com.bolenum.services.user.UserService;
import com.bolenum.services.user.transactions.TransactionService;
import com.bolenum.services.user.wallet.BTCWalletService;
import com.bolenum.services.user.wallet.EtherumWalletService;
import com.bolenum.util.ErrorCollectionUtil;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;

/**
 * @Author Himanshu
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
	BTCWalletService btcWalletService;
	
	@Autowired
	EtherumWalletService etherumWalletService;
	
	@Autowired
	private CountryAndStateService countryAndStateService;
	
	@Autowired
	private TransactionService transactionService;
	
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
			try {
				ObjectMapper mapper = new ObjectMapper();
				String requestObj = mapper.writeValueAsString(signupForm);
				logger.debug("Requested Object:" + requestObj);
				User isUserExist = userService.findByEmail(signupForm.getEmailId());
				if (isUserExist == null) {
					User user = signupForm.copy(new User());
					userService.registerUser(user);
					return ResponseHandler.response(HttpStatus.OK, false,
							localService.getMessage("user.registarion.success"), user.getEmailId());
				} else if (isUserExist != null && isUserExist.getIsEnabled()) {
					return ResponseHandler.response(HttpStatus.CONFLICT, false,
							localService.getMessage("email.already.exist"), isUserExist.getEmailId());
				} else {
					User user = signupForm.copy(new User());
					user.setUserId(isUserExist.getUserId());
					requestObj = mapper.writeValueAsString(user);
					logger.debug("Requested Object for Re Register", user);
					userService.reRegister(user);
					return ResponseHandler.response(HttpStatus.OK, false,
							localService.getMessage("user.registarion.success"), user.getEmailId());
				}
			} catch (JsonProcessingException e) {
				return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
						localService.getMessage("message.error"), null);
			}
		}
	}

	/**
	 * for mail verify at the time of sign up user as well as re register
	 * 
	 * @param token
	 * @return
	 */
	@RequestMapping(value = UrlConstant.USER_MAIL_VERIFY, method = RequestMethod.GET)
	public ResponseEntity<Object> userMailVerfy(@RequestParam("token") String token) {
		logger.debug("user mail verify token: {}", token);
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException(localService.getMessage("token.invalid"));
		} else {
			AuthenticationToken authenticationToken = authenticationTokenService.findByToken(token);
			if (authenticationToken == null) {
				logger.debug("user mail verify authenticationToken is null");
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, false, localService.getMessage("token.invalid"),
						null);
			}
			boolean isExpired = authenticationTokenService.isTokenExpired(authenticationToken);
			logger.debug("user mail verify token expired: {}", isExpired);
			if(isExpired){
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, false, localService.getMessage("token.expired"), null);
			}
		    User user = authenticationToken.getUser();
		    etherumWalletService.createWallet(user);
		    String uuid = btcWalletService.createHotWallet(String.valueOf(user.getUserId()));
			logger.debug("user mail verify wallet uuid: {}", uuid);
		    if (!uuid.isEmpty()) {
		    	user.setBtcWalletUuid(uuid);
		    	user.setBtcWalletAddress(btcWalletService.getWalletAddress(uuid));
				user.setIsEnabled(true);
				User savedUser = userService.saveUser(user);
				logger.debug("user mail verify savedUser: {}", savedUser);
				if (savedUser != null) {
					return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), null);
				}else {
					return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true, localService.getMessage("message.error"), null);
				}
			}else {
				return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true, localService.getMessage("message.error"), null);
			}
		}
	}
	
	/**
	 * for change password
	 * 
	 * @param passwordForm
	 * @param result
	 * @return
	 */

	// @PreAuthorize("hasRole('USER')")
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
	 * 
	 * @return
	 */
	@RequestMapping(value = UrlConstant.GET_LOGGEDIN_USER, method = RequestMethod.GET)
	public ResponseEntity<Object> getLoggedinUser() {
		User user = GenericUtils.getLoggedInUser();
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), user);
	}

	@RequestMapping(value = UrlConstant.ADD_MOBILE_NUMBER, method = RequestMethod.PUT)
	public ResponseEntity<Object> addMobileNumber(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("countryCode") String countryCode)
			throws PersistenceException {
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
	 * 
	 * @param otp
	 * @return
	 * @throws PersistenceException
	 * @throws InvalidOtpException
	 */
	@RequestMapping(value = UrlConstant.VERIFY_OTP, method = RequestMethod.PUT)
	public ResponseEntity<Object> verify(@RequestParam("otp") Integer otp)
			throws PersistenceException, InvalidOtpException {
		User user = GenericUtils.getLoggedInUser();
		Boolean response = userService.verifyOTP(otp, user);
		if (response) {
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("otp.verified"), null);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("otp.not.verified"),
					response);
		}
	}

	/**
	 * 
	 * @return
	 * @throws PersistenceException
	 * @throws InvalidOtpException
	 */
	@RequestMapping(value = UrlConstant.RESEND_OTP, method = RequestMethod.POST)
	public ResponseEntity<Object> resendOtp() throws PersistenceException, InvalidOtpException {
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
	@RequestMapping(value = UrlConstant.GET_COUNTRIES_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getCountriesList() {
		List<Countries> list = countryAndStateService.getCountriesList();
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("all.countries.list"), list);
	}
	
	/**
	 * to get list of states with respect to specific country 
	 * 
	 * @param countryId
	 * @return
	 */
	@RequestMapping(value = UrlConstant.GET_STATE_BY_COUNTRY_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getStatesByCountryId(@RequestParam("countryId") Long countryId) {
		List<States> list = countryAndStateService.getStatesByCountry(countryId);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("states.list.by.country.id"), list);
	}
	
	
	@RequestMapping(value = UrlConstant.GET_TRANSACTION_LIST_OF_USER_WITHDRAW, method = RequestMethod.GET)
	public ResponseEntity<Object> getWithdrawTransactionList(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortOrder") String sortOrder,
			@RequestParam("sortBy") String sortBy) {
		User user = GenericUtils.getLoggedInUser();
		Page<Transaction> listOfUserTransaction = transactionService.getListOfUserTransaction(user,TransactionStatus.WITHDRAW,pageNumber,pageSize,sortOrder,sortBy);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("all.countries.list"),listOfUserTransaction);
	}
}
