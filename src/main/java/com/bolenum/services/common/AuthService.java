/**
 * 
 */
package com.bolenum.services.common;

import java.util.Map;

import com.bolenum.dto.common.ResetPasswordForm;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public interface AuthService {

	/**
	 * 
	 * @param token
	 * @return boolean
	 */

	boolean logOut(String token);

	AuthenticationToken login(String email, User user, String ipAddress, String browserName, String clientOSName)
			throws InvalidPasswordException;

	public boolean validateUser(String email);
	
	public void sendTokenToResetPassword(String email);


	void resetPassword(User verifiedUser, ResetPasswordForm resetPasswordForm);

	User verifyTokenForResetPassword(String token);

	Map<String, Object> loginResponse(AuthenticationToken token);

}
