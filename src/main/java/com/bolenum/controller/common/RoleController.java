package com.bolenum.controller.common;
/**
 * @Author Himanshu
 * @Date 08-Sep-2017
 */ 
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

import com.bolenum.constant.Message;
import com.bolenum.constant.UrlConstant;
import com.bolenum.model.Role;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = UrlConstant.BASE_ROLE_URI_V1)
@Api(value = "User Role Controller", description = "User Role releated methods")
public class RoleController {

	public static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    /**
     * 
     * @param role
     * @param result
     * @return response
     */
	@RequestMapping(value = UrlConstant.ROLE_URI, method = RequestMethod.POST)
	public ResponseEntity<Object> addRole(@Valid @RequestBody Role role, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.CONFLICT, true, Message.ERROR, null);
		} else {
			return ResponseHandler.response(HttpStatus.ACCEPTED, false, Message.ROLE_ADDED_SUCCESSFULLY, null);
		}
	}

	@RequestMapping(value = UrlConstant.ROLE_URI, method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteRole(@Valid @RequestBody Role role, BindingResult result) {

		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.CONFLICT, true, Message.ERROR, null);
		} else {
			return ResponseHandler.response(HttpStatus.ACCEPTED, false, Message.ROLE_ADDED_SUCCESSFULLY, null);
		}
	}
}
