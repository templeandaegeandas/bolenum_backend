package com.bolenum.services.user;

import com.bolenum.model.AuthenticationToken;

public interface AuthenticationTokenService {

	AuthenticationToken findByToken(String token);

	boolean isTokenExpired(AuthenticationToken verificationTokenToCheck);

	Long countActiveUsers();
	
}
