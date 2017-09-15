package com.bolenum.util;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bolenum.model.User;

/**
 * This class has only one method that is used for getting loggedin user form
 * the SecurityContextHolder
 * 
 * @author Vishal Kumar and Ajit Soman
 *
 */

public class GenericUtils {

	public static User getLoggedInUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	public static boolean isValidMail(String email) {
		if (email == null || "".equals(email))
			return false;

		email = email.trim();

		EmailValidator ev = EmailValidator.getInstance();
		return ev.isValid(email);
	}
}
