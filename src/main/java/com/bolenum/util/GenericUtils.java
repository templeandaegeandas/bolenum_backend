package com.bolenum.util;

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
}
