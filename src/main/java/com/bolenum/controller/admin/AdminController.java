package com.bolenum.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * @Author Himanshu Kumar
 *
 * @Date  05-Sep-2017
 */
public class AdminController {
	


	public static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@RequestMapping()
	public ResponseEntity<Object> index() {
	return null;
	}

	

}
