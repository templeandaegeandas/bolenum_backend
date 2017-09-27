package com.bolenum.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		//EmailValidator ev = EmailValidator.getInstance();
		//return ev.isValid(email);
		String emailPattern = "^(.+)@(.+)$";
		Pattern p = Pattern.compile(emailPattern);
		Matcher m = p.matcher(email);
		return m.matches();
	}
}
