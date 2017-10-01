/**
 * 
 */
package com.bolenum.controller.common;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.TwoFactorAuthOption;
import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.LoginForm;
import com.bolenum.dto.common.ResetPasswordForm;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;
import com.bolenum.services.common.AuthService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.user.TwoFactorAuthService;
import com.bolenum.services.user.UserService;
import com.bolenum.util.ErrorCollectionUtil;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
@RestController
@Api(value = "Authentication Controller")
public class AuthController {
	@Autowired
	private AuthService authService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private TwoFactorAuthService twoFactorAuthService;

	@Autowired
	private LocaleService localService;
	public static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	/**
	 * 
	 * @param loginForm
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = UrlConstant.USER_LOGIN, method = RequestMethod.POST)
	ResponseEntity<Object> login(@Valid @RequestBody LoginForm loginForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(bindingResult),
					null);
		} else {
			logger.debug("login email id: {}", loginForm.getEmailId());
			User user = userService.findByEmail(loginForm.getEmailId());
			if (user == null) {
				return ResponseHandler.response(HttpStatus.UNAUTHORIZED, true,
						localService.getMessage("user.not.found"), null);
			} else if (!user.getIsEnabled()) {
				return ResponseHandler.response(HttpStatus.UNAUTHORIZED, true,
						localService.getMessage("user.mail.verify.error"), null);
			} else {
				AuthenticationToken token;
				if (user.getRole().getName().equals(loginForm.getRole())) {
					if (user.getTwoFactorAuthOption().equals(TwoFactorAuthOption.NONE)) {
						try {
							token = authService.login(loginForm.getPassword(), user, loginForm.getIpAddress(),
									loginForm.getBrowserName(), loginForm.getClientOsName());
						} catch (UsernameNotFoundException | InvalidPasswordException e) {
							return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, e.getMessage(), null);
						}
						return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("login.success"),
								authService.loginResponse(token));
					} else if(user.getTwoFactorAuthOption().equals(TwoFactorAuthOption.MOBILE)){
						twoFactorAuthService.sendOtpForTwoFactorAuth(user);
						return ResponseHandler.response(HttpStatus.ACCEPTED, false,
								localService.getMessage("enter.otp"), null);
					}
					else {
						return ResponseHandler.response(HttpStatus.ACCEPTED, false,
								localService.getMessage("enter.otp"), null);
					}

				} else {
					return ResponseHandler.response(HttpStatus.UNAUTHORIZED, true,
							localService.getMessage("user.not.authorized.error"), null);
				}
			}
		}
	}

	/**
	 * controller that respond when hit comes for logout activity of user
	 * 
	 * @param token
	 * @return
	 */
	// @PreAuthorize("hasRole('USER')")
	@RequestMapping(value = UrlConstant.USER_LOOUT, method = RequestMethod.DELETE)
	ResponseEntity<Object> logout(@RequestHeader("Authorization") String token) {
		boolean response = authService.logOut(token);
		if (response) {
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("logout.success"), null);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("logout.failure"),
					null);
		}
	}

	/**
	 * for forget password
	 * 
	 * @param email
	 * @return
	 */
	@RequestMapping(value = UrlConstant.FORGET_PASS, method = RequestMethod.GET)
	public ResponseEntity<Object> forgetPassword(@RequestParam String email) {
		boolean isValid = GenericUtils.isValidMail(email);

		if (isValid) {
			boolean isValidUser = authService.validateUser(email);

			if (isValidUser) {
				authService.sendTokenToResetPassword(email);
				return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("mail.sent.success"),
						email);
			}
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.email"), null);
	}

	/**
	 * to verify authentication link send at the time of forget password
	 * 
	 * @param token
	 * @param resetPasswordForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = UrlConstant.FORGET_PASS_VERIFY, method = RequestMethod.PUT)
	public ResponseEntity<Object> resetPassword(@RequestParam String token,
			@Valid @RequestBody ResetPasswordForm resetPasswordForm, BindingResult result) {
		logger.debug("user mail verify token: {}", token);
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException(localService.getMessage("token.invalid"));
		}
		User verifiedUser = authService.verifyTokenForResetPassword(token);
		if (verifiedUser == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("token.invalid"),
					null);
		} else if (!result.hasErrors() && verifiedUser != null) {
			authService.resetPassword(verifiedUser, resetPasswordForm);
			return ResponseHandler.response(HttpStatus.OK, false,
					localService.getMessage("user.password.change.success"), verifiedUser.getEmailId());
		} else {
			return ResponseHandler.response(HttpStatus.CONFLICT, true,
					localService.getMessage("user.password.not.matched"), null);
		}
	}

}
