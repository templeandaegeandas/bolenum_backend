package com.bolenum.util;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bolenum.model.User;

/**
 * @author Vishal Kumar
 * @date 14-sep-2017
 */

public class GenericUtils {

	/**
	 * 
	 * @return user
	 */

	public static User getLoggedInUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public static boolean isValidMail(String email) {
		EmailValidator ev = EmailValidator.getInstance();
		return ev.isValid(email);
	}
}
