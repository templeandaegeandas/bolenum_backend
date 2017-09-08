package com.bolenum.controller.common;

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
import com.bolenum.controller.user.UserController;
import com.bolenum.model.Privilege;
import com.bolenum.service.common.PrivilegeService;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = UrlConstant.BASE_PRIVILEGE_URI_V1)
@Api(value = "Privileges Controller", description = "Privileges releated methods")

public class PrivilegesController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private PrivilegeService privilegeService;
	
	@RequestMapping(value = UrlConstant.PRIVILEGE_URI, method = RequestMethod.POST)
	public ResponseEntity<Object> addPrivileges(@Valid @RequestBody Privilege privilege, BindingResult result) {
		 if(result.hasErrors())
		 {
			    logger.error("message logged at error level");
		        return ResponseHandler.response(HttpStatus.ACCEPTED,true, Message.ERROR, null);	 
	     }
		 else
		 {
			 privilegeService.savePrivilege(privilege);
			 return ResponseHandler.response(HttpStatus.ACCEPTED,false, Message.PRIVILEGE_ADDED_SUCCESSFULLY, null);
		 }
		 
	}
	
	@RequestMapping(value = UrlConstant.PRIVILEGE_URI, method = RequestMethod.DELETE)
	public ResponseEntity<Object> deletePrivileges(@RequestBody String name, BindingResult result) {
		
		 privilegeService.deletePrivilege(name);
		 return ResponseHandler.response(HttpStatus.ACCEPTED,false, Message.PRIVILEGE_REMOVED_SUCCESSFULLY, null);	 
	}
	
}
