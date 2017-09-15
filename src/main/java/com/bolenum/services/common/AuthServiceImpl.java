/**
 * 
 */
package com.bolenum.services.common;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
	private MailService emailService; 
	
	@Override
	public AuthenticationToken login(String email, String password) throws InvalidPasswordException {
		User user = userRepository.findByEmailIdIgnoreCase(email);
		if (user != null) {
			if (passwordEncoder.matches(password, user.getPassword())) {
				// Generate Token and Save it for the logged in user
				AuthenticationToken authToken = new AuthenticationToken(TokenGenerator.generateToken(), user);
				authToken.setTokentype(TokenType.AUTHENTICATION);
				AuthenticationToken savedAuthToken = authenticationTokenRepo.save(authToken);
				return savedAuthToken;
			} else {
				throw new InvalidPasswordException(Message.INVALID_CRED);
			}
		} else {
			throw new UsernameNotFoundException(Message.USER_NOT_FOUND);
		}
	}


	@Override
	public boolean validateUser(String email) {
		User user = userRepository.findByEmailIdIgnoreCase(email);
		if (user != null) {
			return true;
		}
		return false;
	}

	public boolean isValidMail(String email) {
		if (email == null || "".equals(email))
			return false;

		email = email.trim();

		EmailValidator ev = EmailValidator.getInstance();
		return ev.isValid(email);
	}

	public void sendTokenToResetPassword(String email)
	{
		
		User existingUser=userRepository.findByEmailIdIgnoreCase(email);
		AuthenticationToken previousToken = authenticationTokenRepo.findByUser(existingUser);
		authenticationTokenRepo.delete(previousToken);
		String token = TokenGenerator.generateToken();
		AuthenticationToken authenticationToken = new AuthenticationToken(token,existingUser);
		emailService.mailSend(email, token);
		authenticationToken.setTokentype(TokenType.FORGOT_PASSWORD);
		authenticationTokenRepo.saveAndFlush(authenticationToken);
		
	}
	
	@Override
	public boolean resetPassword(String email) {
		
		return false;

	}
	
	
	
}
