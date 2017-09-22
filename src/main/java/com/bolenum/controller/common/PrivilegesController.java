package com.bolenum.controller.common;

import java.util.List;
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

import com.bolenum.constant.UrlConstant;
import com.bolenum.controller.user.UserController;
import com.bolenum.model.Privilege;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.common.PrivilegeService;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * @Author himanshu
 * @Date 08-Sep-2017
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_PRIVILEGE_URI_V1)
@Api(value = "Privileges Controller")

public class PrivilegesController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private PrivilegeService privilegeService;

	@Autowired
	private LocaleService localService;

	/**
	 * to add privileges
	 * 
	 * @param privilege
	 * @param result
	 * @return response
	 */
	@RequestMapping(value = UrlConstant.PRIVILEGE_URI, method = RequestMethod.POST)
	public ResponseEntity<Object> addPrivileges(@Valid @RequestBody Privilege privilege, BindingResult result) {
		if (result.hasErrors()) {
			logger.error("message logged at error level");
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("message.error"),
					null);
		} else {

			privilegeService.savePrivilege(privilege);
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("privilege.add.success"),
					null);
		}
	}

	/**
	 * 
	 * to delete privileges
	 * 
	 * @param id
	 * @return response
	 */
	@RequestMapping(value = UrlConstant.PRIVILEGE_URI, method = RequestMethod.DELETE)
	public ResponseEntity<Object> deletePrivileges(Long id) {
		Boolean response = privilegeService.deletePrivilege(id);
		if (response) {
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("privilege.remove.success"),
					"requested privileges removed successfully");
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localService.getMessage("privilege.not.found"), "please request valid privilege");
		}
	}

	/**
	 * 
	 * to view privileges
	 * 
	 * @param id
	 * @return response
	 * 
	 */
	@RequestMapping(value = UrlConstant.PRIVILEGE_URI, method = RequestMethod.GET)
	public ResponseEntity<Object> getPrivileges() {
		List<Privilege> listOfPrivilege = privilegeService.findAllPrevileges();
		if (listOfPrivilege != null) {
			return ResponseHandler.response(HttpStatus.ACCEPTED, false,
					localService.getMessage("privilege.found.success"), listOfPrivilege);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localService.getMessage("privilege.not.found"), "please request valid privilege");
		}
	}

}
