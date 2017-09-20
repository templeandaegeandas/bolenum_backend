package com.bolenum.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.model.User;
import com.bolenum.services.admin.AdminService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

/**
 * @Author Himanshu Kumar
 *
 * @Date 05-Sep-2017
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_ADMIN_URI_V1)
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private LocaleService localeService;

	public static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@RequestMapping()
	public ResponseEntity<Object> index() {
		return null;
	}

	@RequestMapping(value = UrlConstant.LIST_USERS, method = RequestMethod.GET)
	public ResponseEntity<Object> getUsersList(@RequestParam int pageNumber, @RequestParam int pageSize) {
		User user = GenericUtils.getLoggedInUser();
		Page<User> userList = adminService.getUsersList(pageNumber, pageSize, user);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("admin.user.list"), userList);
	}
	
	@RequestMapping(value = UrlConstant.GET_USER_BY_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getUsersById(@PathVariable Long userId) {
		User user = adminService.getUserById(userId);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("admin.user.get.by.id"), user);
	}
	
}
