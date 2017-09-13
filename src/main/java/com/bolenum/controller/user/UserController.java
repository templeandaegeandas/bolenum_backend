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
	public ResponseEntity<Object> registerUser(@Valid @RequestBody UserSignupForm userForm, BindingResult result) throws  JsonProcessingException {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.INVALID_REQ, ErrorCollectionUtil.getErrorMap(result));
		} else {
			User isUserExist = userService.findByEmail(userForm.getEmailId());
			if (isUserExist == null) {
				User user = userForm.copy(new User());
				ObjectMapper mapper = new ObjectMapper();
				String requestBean = mapper.writeValueAsString(user);
				System.out.println(requestBean);
				userService.registerUser(user);
				return ResponseHandler.response(HttpStatus.OK, false, Message.SUCCESS, user.getEmailId());
			} else if (isUserExist != null && isUserExist.getIsEnabled()) {
				return ResponseHandler.response(HttpStatus.CONFLICT, false, Message.EMAIL_ALREADY_EXISTS,
						isUserExist.getEmailId());
			} else {
				User user = userForm.copy(new User());
				user.setUserId(isUserExist.getUserId());
				userService.reRegister(user);
				return ResponseHandler.response(HttpStatus.OK, false, Message.EMAIL_ALREADY_EXISTS, user.getEmailId());
			}

		}
	}

	@RequestMapping(value = UrlConstant.USER_MAIL_VERIFY, method = RequestMethod.GET)
	public ResponseEntity<Object> userMailVerfy(@Valid @RequestBody User user, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.INVALID_EMAIL, null);
		} else {
			User isUserExist = userService.findByEmail(user.getEmailId());
			if (isUserExist == null) {
				userService.registerUser(user);
				return ResponseHandler.response(HttpStatus.OK, false, Message.SUCCESS, user.getEmailId());
			} else if (isUserExist != null && isUserExist.getIsEnabled()) {
				return ResponseHandler.response(HttpStatus.CONFLICT, false, Message.EMAIL_ALREADY_EXISTS,
						user.getEmailId());
			} else {
				return ResponseHandler.response(HttpStatus.OK, false, Message.EMAIL_ALREADY_EXISTS, user.getEmailId());
			}

		}
	}

}
