package com.bolenum.controller.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;

/**
 * @Author chandan Kumar Singh
 *
 * @Date  04-Sep-2017
 */

@RestController
@RequestMapping(value=UrlConstant.BASE_USER_URI_V1)
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);
 
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<String>> listAllUsers() {
    	List<String> list=new ArrayList<>();
    	list.add("Chandan");
    	list.add("Kumar");
    	list.add("Singh");
        return new ResponseEntity<List<String>>(list, HttpStatus.OK);
    }
 
}
