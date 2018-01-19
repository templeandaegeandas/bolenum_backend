/*@Description Of interface
 * 
 * AuthService interface is responsible for below listed task: 
 *  	
 *  	LogOut
 *      Login
 *      Validate user
 *      Send token to reset password
 *      Reset password
 *      Verify token for reset password
 *      Login response
 *      
 */
package com.bolenum.services.common;

import java.util.Map;

import com.bolenum.dto.common.ResetPasswordForm;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public interface AuthService {

	/**@description use to get user logout
	 * @param token
	 * @return boolean
	 */

	boolean logOut(String token);
	
	/**@description use to get user login
	 * @param email
	 * @param user
	 * @param ipAddress
	 * @param browserName
	 * @param clientOSName
	 * @return AuthenticationToken
	 */

	AuthenticationToken login(String email, User user, String ipAddress, String browserName, String clientOSName);
	
	/**@description use to get user validate
	 * @param email
	 * @return user
	 */
	public User validateUser(String email);
	
	/**@description use to send token so that user can reset password
	 * @param user
	 * @return AuthenticationToken
	 */
	public AuthenticationToken sendTokenToResetPassword(User user);
	
	
	/**@description use to reset password
	 * @param verifiedUser
	 * @param resetPasswordForm
	 */
	void resetPassword(User verifiedUser, ResetPasswordForm resetPasswordForm);
	
	/**@description use to verify token for reset password
	 * @param token
	 * @return User
	 */
	User verifyTokenForResetPassword(String token);
	
	/**@description use to send response after login
	 * @param token
	 * @return Map
	 */

	Map<String, Object> loginResponse(AuthenticationToken token);

}
