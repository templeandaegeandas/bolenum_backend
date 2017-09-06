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
import com.bolenum.constant.Message;
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

	 @RequestMapping(value = "/adduser", method = RequestMethod.GET)
	 public ResponseEntity<Object> addUser(@Valid @RequestBody User user, BindingResult result) {
		
		 if(result.hasErrors())
		 {
			 return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.USER_INVALID_EMAIL, null); 
		 }
		 else
		 {
			  /*Boolean isUserExist = userService.userIsExist(user);
			  if (!isUserExist)
			  {
				  
			  }
			  else
			  {
				  
			  }*/
			  return null;
		 }
	
	}
}
