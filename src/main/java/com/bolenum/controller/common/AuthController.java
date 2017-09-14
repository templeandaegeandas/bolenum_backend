/**
 * 
 */
package com.bolenum.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.Message;
import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.LoginForm;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.services.common.AuthService;
import com.bolenum.util.ResponseHandler;
import org.apache.commons.validator.routines.EmailValidator;

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
			return ResponseHandler.response(HttpStatus.OK, false, Message.LOGIN_SUCCESS, loginResponse(token));
		}
	}

	private Map<String, Object> loginResponse(AuthenticationToken token) {
		Map<String, Object> map = new HashMap<>();
		map.put("fName", token.getUser().getFirstName());
		map.put("mName", token.getUser().getMiddleName());
		map.put("lName", token.getUser().getLastName());
		map.put("name", token.getUser().getFullName());
		map.put("email", token.getUser().getEmailId());
		map.put("role", token.getUser().getRole().getName());
		map.put("token", token.getToken());
		return map;
	}

	@RequestMapping(value = UrlConstant.FORGET_PASSWORD, method = RequestMethod.GET)
	public ResponseEntity<Object> forgetPassword(@RequestParam String email) {
		boolean valid = EmailValidator.getInstance().isValid(email);
		if (valid) {
			//authService.validateUser(email);
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.INVALID_EMAIL, null);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, Message.INVALID_EMAIL, null);
		}
	}

}
