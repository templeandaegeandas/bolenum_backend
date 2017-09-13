/**
 * 
 */
package com.bolenum.controller.common;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.Message;
import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.LoginForm;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.services.common.AuthService;
import com.bolenum.util.ResponseHandler;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
@RestController
public class AuthController {
	@Autowired
	private AuthService authService;

	@RequestMapping(value = UrlConstant.USER_LOGIN, method = RequestMethod.POST)
	ResponseEntity<Object> login(@Valid @RequestBody LoginForm loginForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.INVALID_EMAIL, null);
		} else {
			AuthenticationToken token;
			try {
				token = authService.login(loginForm.getEmailId(), loginForm.getPassword());
			} catch (UsernameNotFoundException | InvalidPasswordException e) {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, e.getMessage(), null);
			}
			return ResponseHandler.response(HttpStatus.OK, false, "Login sucessful", token);

		}
	}
}
