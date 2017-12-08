package com.bolenum.controller.common;

/**
 * @Author Himanshu
 * @Date 08-Sep-2017
 */

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.model.Role;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.common.RoleService;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = UrlConstant.BASE_ROLE_URI_V1)
@Api(value = "User Role Controller")
@Scope("request")
public class RoleController {

	public static final Logger logger = LoggerFactory.getLogger(RoleController.class);

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private LocaleService localService;

	/**
	 * to add role
	 * 
	 * @param role
	 * @param result
	 * @return response
	 */

	@RequestMapping(value = UrlConstant.ROLE_URI, method = RequestMethod.POST)
	public ResponseEntity<Object> addRoles(@Valid @RequestBody Role role, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("message.error"), null);
		} else {
			roleService.saveRole(role);
			return ResponseHandler.response(HttpStatus.ACCEPTED, false, localService.getMessage("role.add.success"),
					role.getName());
		}
	}

	/**
	 * to delete roles
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = UrlConstant.ROLE_URI, method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteRoles(Long id) {
		Boolean response = roleService.deleteRole(id);
		if (response) {
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("privilege.remove.success"),
					"requested role removed successfully");
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("role.not.found"),
					"please request valid role");
		}
	}
	
	/**
	 * to view roles
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = UrlConstant.ROLE_URI, method = RequestMethod.GET)
	public ResponseEntity<Object> getRoles(Long id) {
		Role role = roleService.viewRole(id);
		if (role != null) {
			return ResponseHandler.response(HttpStatus.ACCEPTED, false, localService.getMessage("role.found"), role.getName());
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,localService.getMessage("role.not.found"),
					"please request valid role");
		}
	}
	
	


}
