package com.bolenum.controller.common;

/**
 * @Author Himanshu
 * @Date 08-Sep-2017
 */

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
import com.bolenum.model.Role;
import com.bolenum.service.common.RoleService;
import com.bolenum.util.ResponseHandler;
import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = UrlConstant.BASE_ROLE_URI_V1)
@Api(value = "User Role Controller", description = "User Role releated methods")
public class RoleController {

	public static final Logger logger = LoggerFactory.getLogger(RoleController.class);

	@Autowired
	private RoleService roleService;

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
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.ERROR, null);
		} else {
			roleService.saveRole(role);
			return ResponseHandler.response(HttpStatus.ACCEPTED, false, Message.ROLE_ADDED_SUCCESSFULLY,
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
			return ResponseHandler.response(HttpStatus.OK, false, Message.PRIVILEGE_REMOVED_SUCCESSFULLY,
					"requested role removed successfully");
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.ROLE_NOT_FOUND,
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
			return ResponseHandler.response(HttpStatus.ACCEPTED, false, Message.ROLE_FOUND, role.getName());
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.ROLE_NOT_FOUND,
					"please request valid role");
		}
	}
	
	
	/**
	 * to update roles
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = UrlConstant.ROLE_URI, method = RequestMethod.PUT)
	public ResponseEntity<Object> updateRoles(Long id) {
		Role role = roleService.updateRole(id);
		if (role != null) {
			return ResponseHandler.response(HttpStatus.ACCEPTED, false, Message.ROLE_FOUND, role.getName());
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.ROLE_NOT_FOUND,
					"please request valid role");
		}
	}


}
