package com.bolenum.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * @Author chandan Kumar Singh
 *
 * @Date 04-Sep-2017
 */

@RestController
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
@Api(value = "User Controller", description = "User releated methods")
public class UserController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> listAllUsers() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "chandan");
		return ResponseHandler.response(map, HttpStatus.OK, "list users", false,null);
	}
}
