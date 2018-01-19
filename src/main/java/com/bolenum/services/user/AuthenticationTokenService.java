/*@Description Of Class
 * 
 * AuthenticationTokenService interface is responsible for below listed task: 
 *   
 *    Find token
 *    Check token expired or not.
 *    Count active users with in seven days.    
 **/

package com.bolenum.services.user;

import com.bolenum.model.AuthenticationToken;

public interface AuthenticationTokenService {

	/**@Description find token by token name 
	 * @param token
	 */
	AuthenticationToken findByToken(String token);

	/**@Description to verify the token expired or not
	 * @param verificationTokenToCheck
	 */
	boolean isTokenExpired(AuthenticationToken verificationTokenToCheck);

	/**@Description use to count active user
	 */
	Long countActiveUsers();
	
}
