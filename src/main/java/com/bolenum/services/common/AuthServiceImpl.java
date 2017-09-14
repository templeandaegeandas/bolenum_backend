/**
 * 
 */
package com.bolenum.services.common;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bolenum.constant.Message;
import com.bolenum.constant.TokenType;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;
import com.bolenum.model.UserActivity;
import com.bolenum.repo.common.AuthenticationTokenRepo;
import com.bolenum.repo.user.UserActivityRepository;
import com.bolenum.repo.user.UserRepository;
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
	private UserActivityRepository userActivityRepository;

	@Override
	public AuthenticationToken login(String email, String password, String ipAddress, String browserName)
			throws InvalidPasswordException {
		User user = userRepository.findByEmailIdIgnoreCase(email);
		if (user != null) {
			if (passwordEncoder.matches(password, user.getPassword())) {
				// Generate Token and Save it for the logged in user
				AuthenticationToken authToken = new AuthenticationToken(TokenGenerator.generateToken(), user);
				authToken.setTokentype(TokenType.AUTHENTICATION);
				AuthenticationToken savedAuthToken = authenticationTokenRepo.save(authToken);
				UserActivity userActivity = new UserActivity(ipAddress, browserName, savedAuthToken);
				userActivityRepository.save(userActivity);
				return savedAuthToken;
			} else {
				throw new InvalidPasswordException(Message.INVALID_CRED);
			}
		} else {
			throw new UsernameNotFoundException(Message.USER_NOT_FOUND);
		}
	}

	@Override
	public boolean logOut(String token) {
		AuthenticationToken authToken = authenticationTokenRepo.findByToken(token);
		if (authToken != null && authToken.getDeletedOn() == null) {
			UserActivity userActivity = userActivityRepository.findByAuthenticationToken(authToken);
			userActivity.setIsDeleted(true);
			userActivityRepository.save(userActivity);
			authToken.setDeletedOn(new Date());
			authenticationTokenRepo.save(authToken);
			return true;
		}
		else {
			return false;
		}
	}

}
