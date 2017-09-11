package com.bolenum.controller.user;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.model.User;
import com.bolenum.constant.UrlConstant;
import io.swagger.annotations.Api;

/**
 * @Author chandan Kumar Singh
 *
 * @Date 04-Sep-2017
 */

@RestController
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
@Api(value = "User Controller")
public class UserController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@RequestMapping(value = "/adduser", method = RequestMethod.GET)
	public ResponseEntity<Object> addUser(@Valid @RequestBody User user, BindingResult result) {

		return null;
	}

}
