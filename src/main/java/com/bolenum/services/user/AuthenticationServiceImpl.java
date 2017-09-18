package com.bolenum.services.user;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;
import com.bolenum.repo.common.AuthenticationTokenRepo;
import com.bolenum.repo.user.UserRepository;

@Transactional
@Service
public class AuthenticationServiceImpl implements AuthenticationTokenService {

	@Autowired
	private AuthenticationTokenRepo authenticationTokenRepo;

	@Autowired
	private UserRepository userRepo;

	@Override
	public boolean verifyUserToken(String token) {
		if (token != null && !token.isEmpty()) {
			AuthenticationToken authenticationToken = authenticationTokenRepo.findByToken(token);
			if (authenticationToken != null) {
				User user = authenticationToken.getUser();
				boolean isExpired = isTokenExpired(authenticationToken);
				if (user != null && user.getIsEnabled() == false && isExpired == false) {
					user.setIsEnabled(true);
					userRepo.saveAndFlush(user);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isTokenExpired(AuthenticationToken verificationTokenToCheck) {

		Date tokenCreatedTime = verificationTokenToCheck.getCreatedOn();
		long HOUR = 3600 * 1000;
		long expirationTime = tokenCreatedTime.getTime() + (2 * HOUR);
		if (expirationTime > tokenCreatedTime.getTime()) {
			return false;
		} else {
			return true;
		}
	}
}
