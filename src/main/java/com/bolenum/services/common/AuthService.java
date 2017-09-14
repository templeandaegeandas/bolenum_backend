/**
 * 
 */
package com.bolenum.services.common;

import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public interface AuthService {

	boolean logOut(String token);

	AuthenticationToken login(String email, User user, String ipAddress, String browserName)
			throws InvalidPasswordException;

	Boolean resetPassword(String email);

	void validateUser(String email);
}
