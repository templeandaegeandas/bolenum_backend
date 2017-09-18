package com.bolenum.services.user;

import com.bolenum.model.AuthenticationToken;

public interface AuthenticationTokenService {

	boolean verifyUserToken(String token);

	boolean isTokenExpired(AuthenticationToken verificationTokenToCheck);

}
