package com.bolenum.controller.user;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.model.User;
import com.bolenum.services.user.UserService;
import com.bolenum.util.ResponseHandler;
import com.bolenum.constant.Message;
import com.bolenum.constant.UrlConstant;
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

	@RequestMapping(value = "/adduser", method = RequestMethod.GET)
	public ResponseEntity<Object> addUser(@Valid @RequestBody User user, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.INVALID_EMAIL, null);
		} else {
			Boolean isUserExist = UserService.userIsExist(user);
			if (!isUserExist) {
				UserService.RegisterUser(user);
				// System.out.println(user.getEmailId());
				return ResponseHandler.response(HttpStatus.OK, false, Message.SUCCESS, user.getEmailId());
			} else {
				boolean isRegistered = UserService.userIsAlreadyRegistered(user);
				if (isRegistered) {
					return ResponseHandler.response(HttpStatus.CONFLICT, false, Message.EMAIL_ALREADY_EXISTS,
							user.getEmailId());
				} else {
					UserService.userIsAlreadyRegistered(user);
					return ResponseHandler.response(HttpStatus.OK, false, Message.EMAIL_ALREADY_EXISTS,
							user.getEmailId());
				}

			}
		}

	}
}
