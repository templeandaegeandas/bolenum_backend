/**
 * 
 */
package com.bolenum.services.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.constant.Message;
import com.bolenum.constant.TokenType;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;
import com.bolenum.repo.common.AuthenticationTokenRepo;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.util.MailService;
import com.bolenum.util.PasswordEncoderUtil;
import com.bolenum.util.TokenGenerator;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
@Service
public class AuthServiceImpl implements AuthService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoderUtil passwordEncoder;
	@Autowired
	private AuthenticationTokenRepo authenticationTokenRepo;
	@Autowired
	private MailService emailservice;

	@Override
	public AuthenticationToken login(String password, User user) throws InvalidPasswordException {
		if (passwordEncoder.matches(password, user.getPassword())) {
			// Generate Token and Save it for the logged in user
			AuthenticationToken authToken = new AuthenticationToken(TokenGenerator.generateToken(), user);
			authToken.setTokentype(TokenType.AUTHENTICATION);
			AuthenticationToken savedAuthToken = authenticationTokenRepo.save(authToken);
			return savedAuthToken;
		} else {
			throw new InvalidPasswordException(Message.INVALID_CRED);
		}
	}

	@Override
	public Boolean resetPassword(String email) {
		return null;

	}

	@Override
	public void validateUser(String email) {
		User user = userRepository.findByEmailIdIgnoreCase(email);
		if (user != null) {

		}
		// return false;
	}

}
