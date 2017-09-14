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
	AuthenticationToken login(String email, User user) throws InvalidPasswordException;

	Boolean resetPassword(String email);

	void validateUser(String email);
}
