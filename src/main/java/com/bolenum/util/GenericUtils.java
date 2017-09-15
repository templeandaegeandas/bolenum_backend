package com.bolenum.util;

import org.springframework.security.core.context.SecurityContextHolder;

import com.bolenum.model.User;

/**
 * This class has only one method that is used for getting loggedin user form
 * the SecurityContextHolder
 * 
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
}
