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
import com.bolenum.model.Privilege;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = UrlConstant.BASE_PRIVILEGE_URI_V1)
@Api(value = "Privileges Controller", description = "Privileges releated methods")

public class PrivilegesController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@RequestMapping(value = "/addPrivileges", method = RequestMethod.GET)
	public ResponseEntity<Object> addPrivileges(@RequestBody Privilege privilege, BindingResult result) {
		
		 return ResponseHandler.response(HttpStatus.ACCEPTED,false, Message.PRIVILEGE_ADDED_SUCCESSFULLY, null);
		 
	}
}
