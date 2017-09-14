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

import com.bolenum.constant.Message;
import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.UserSignupForm;
import com.bolenum.model.User;
import com.bolenum.services.user.UserService;
import com.bolenum.util.ErrorCollectionUtil;
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

	@RequestMapping(value = UrlConstant.REGISTER_USER, method = RequestMethod.POST)
	public ResponseEntity<Object> registerUser(@Valid @RequestBody UserSignupForm signupForm, BindingResult result) throws  JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		String requestObj = mapper.writeValueAsString(signupForm);
		logger.debug("Requested Object:",requestObj);
		
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.INVALID_REQ, ErrorCollectionUtil.getErrorMap(result));
		} else {
			User isUserExist = userService.findByEmail(signupForm.getEmailId());
			if (isUserExist == null) {
				User user = signupForm.copy(new User());
				userService.registerUser(user);
				return ResponseHandler.response(HttpStatus.OK, false, Message.REGIS_SUCCESS, user.getEmailId());
			} else if (isUserExist != null && isUserExist.getIsEnabled()) {
				return ResponseHandler.response(HttpStatus.CONFLICT, false, Message.EMAIL_ALREADY_EXISTS,
						isUserExist.getEmailId());
			} else {
				User user = signupForm.copy(new User());
				user.setUserId(isUserExist.getUserId());
				requestObj = mapper.writeValueAsString(user);
				logger.debug("Requested Object for Re Register",user);
				userService.reRegister(user);
				return ResponseHandler.response(HttpStatus.OK, false, Message.REGIS_SUCCESS, user.getEmailId());
			}

		}
	}

	@RequestMapping(value = UrlConstant.USER_MAIL_VERIFY, method = RequestMethod.GET)
	public ResponseEntity<Object> userMailVerfy(@RequestParam String token) {

		if ((token != null) && (!token.isEmpty())) {
			boolean isVerified = userService.verifyUserToken(token);
			if (isVerified) {
				return ResponseHandler.response(HttpStatus.OK, false, Message.SUCCESS, null);
			} else {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, false, Message.INVALID_TOKEN, null);
			}
		}

		else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false, Message.INVALID_TOKEN, null);
		}
	}

}
