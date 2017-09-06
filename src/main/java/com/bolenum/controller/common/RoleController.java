package com.bolenum.controller.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.Message;
import com.bolenum.constant.UrlConstant;
import com.bolenum.controller.user.UserController;
import com.bolenum.model.Role;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = UrlConstant.BASE_ROLE_URI_V1)
@Api(value = "User Role Controller", description = "User Role releated methods")

public class RoleController {
     
	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@RequestMapping(value = "/addUserRole", method = RequestMethod.GET)
	public ResponseEntity<Object> addUserRole(@RequestBody Role role, BindingResult result) {
		
		return ResponseHandler.response(HttpStatus.ACCEPTED,false, Message.ROLE_ADDED_SUCCESSFULLY, null);
		
		
	}
	
	
}

