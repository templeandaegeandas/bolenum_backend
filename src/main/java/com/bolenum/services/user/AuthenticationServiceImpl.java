/*@Description Of Class
 * 
 * AuthenticationServiceImpl class is responsible for below listed task: 
 *   
 *    Find token
 *    Check token expired or not.
 *    Count active users with in seven days.
 *    
 */
package com.bolenum.services.user;

import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.enums.TokenType;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.repo.common.AuthenticationTokenRepo;

@Transactional
@Service
public class AuthenticationServiceImpl implements AuthenticationTokenService {

	@Autowired
	private AuthenticationTokenRepo authenticationTokenRepo;

	
	/**@Description use to find token by name
	 * @param token
	 * @return true/false expired/not expired
	 */
	@Override
	public AuthenticationToken findByToken(String token) {
		return authenticationTokenRepo.findByToken(token);
	}

	/**@Description to verify the token expired or not
	 * @param verificationTokenToCheck
	 * @return true/false expired/not expired
	 */
	@Override
	public boolean isTokenExpired(AuthenticationToken verificationTokenToCheck) {

		Date tokenCreatedTime = verificationTokenToCheck.getCreatedOn();
		long hour = (long) 3600 * 1000;
		long expirationTime = tokenCreatedTime.getTime() + (2 * hour);
		long currentTime = new Date().getTime();
		return currentTime > expirationTime;
	}

	/**@Description to count number of user who logged in within 7 days
	 * @return startDate , endDate
	 */
	@Override
	public Long countActiveUsers() {
		Date endDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.DATE, -7);
		Date startDate = c.getTime();
		return authenticationTokenRepo.countAuthenticationTokenByTokentypeAndCreatedOnBetween(TokenType.AUTHENTICATION,
				startDate, endDate);

	}
}
