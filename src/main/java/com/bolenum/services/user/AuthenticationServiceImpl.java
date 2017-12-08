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

	@Override
	public AuthenticationToken findByToken(String token) {
		return authenticationTokenRepo.findByToken(token);
	}

	/**
	 * to verify the token expired or not
	 * 
	 * @return true/false expired/not expired
	 */
	@Override
	public boolean isTokenExpired(AuthenticationToken verificationTokenToCheck) {

		Date tokenCreatedTime = verificationTokenToCheck.getCreatedOn();
		long HOUR = 3600 * 1000;
		long expirationTime = tokenCreatedTime.getTime() + (2 * HOUR);
		long currentTime = new Date().getTime();
		if (currentTime > expirationTime) {
			return true; // token has expired
		} else {
			return false; // token has not expired
		}
	}

	/**
	 * to count number of user who logged in within 7 days
	 */
	@Override
	public Long countActiveUsers() {
		Date endDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.DATE, -7);
		Date startDate = c.getTime();
		return authenticationTokenRepo.countAuthenticationTokenByTokentypeCreatedOnBetween(TokenType.AUTHENTICATION,
				startDate, endDate);

	}
}
