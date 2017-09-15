/**
 * 
 */
package com.bolenum.services.common;

import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public interface AuthService {
	AuthenticationToken login(String email, String password) throws InvalidPasswordException;

	public boolean resetPassword(String email);

	public boolean validateUser(String email);

	public boolean isValidMail(String email);
	
	public void sendTokenToResetPassword(String email);

}
