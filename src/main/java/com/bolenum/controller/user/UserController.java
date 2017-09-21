package com.bolenum.controller.user;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.User;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.user.AuthenticationTokenService;
import com.bolenum.services.user.UserService;
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

	@RequestMapping(value = UrlConstant.USER_MAIL_VERIFY, method = RequestMethod.GET)
	public ResponseEntity<Object> userMailVerfy(@RequestParam String token) {

		boolean isVerified = authenticationTokenService.verifyUserToken(token);
		if (isVerified) {
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("message.success"), null);
		}

		return ResponseHandler.response(HttpStatus.BAD_REQUEST, false, localService.getMessage("token.invalid"), null);
	}

	// @PreAuthorize("hasRole('USER')")
	@RequestMapping(value = UrlConstant.CHANGE_PASSWORD, method = RequestMethod.PUT)
	public ResponseEntity<Object> changePassword(@Valid @RequestBody PasswordForm passwordForm) {
		User user = GenericUtils.getLoggedInUser();
		boolean response;
		try {
			response = userService.changePassword(user, passwordForm);
		} catch (InvalidPasswordException e) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("invalid.request"),
					null);
		}
		if (response) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localService.getMessage("user.password.change.success"), null);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localService.getMessage("user.password.change.failure"), null);
		}
	}

	@RequestMapping(value = UrlConstant.UPDATE_USER_PROFILE, method = RequestMethod.PUT)
	public ResponseEntity<Object> updateUserProfile(@Valid @RequestBody EditUserForm editUserForm,
			BindingResult result) {
      
		User user = GenericUtils.getLoggedInUser();

		if (!result.hasErrors() && user!=null) {
			userService.updateUserProfile(editUserForm, user);
			return ResponseHandler.response(HttpStatus.OK, false,
					localService.getMessage("user.profile.update.success"), null);

		}
		else
		return ResponseHandler.response(HttpStatus.CONFLICT, true, localService.getMessage("invalid.email"), null);
	}

}
