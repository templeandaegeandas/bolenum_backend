package com.bolenum.services.common;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.constant.TokenType;
import com.bolenum.dto.common.ResetPasswordForm;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;
import com.bolenum.model.UserActivity;
import com.bolenum.repo.common.AuthenticationTokenRepo;
import com.bolenum.repo.user.UserActivityRepository;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.services.user.AuthenticationTokenService;
import com.bolenum.util.MailService;
import com.bolenum.util.PasswordEncoderUtil;
import com.bolenum.util.TokenGenerator;

/**
 * @author Himanshu
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
	
	@Autowired
	private UserActivityRepository userActivityRepository;

	@Autowired
	private AuthenticationTokenService authenticationTokenService;

	@Autowired
	private LocaleService localeService;
	
	@Override
	public AuthenticationToken login(String password, User user, String ipAddress, String browserName)
			throws InvalidPasswordException {
		if (passwordEncoder.matches(password, user.getPassword())) {
			// Generate Token and Save it for the logged in user
			AuthenticationToken authToken = new AuthenticationToken(TokenGenerator.generateToken(), user);
			authToken.setTokentype(TokenType.AUTHENTICATION);
			AuthenticationToken savedAuthToken = authenticationTokenRepo.save(authToken);
			UserActivity userActivity = new UserActivity(ipAddress, browserName, savedAuthToken);
			userActivityRepository.save(userActivity);
			return savedAuthToken;
		} else {
			throw new InvalidPasswordException(localeService.getMessage("invalid.credential"));
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
		} else {
			return false;
		}
	}

	public void sendTokenToResetPassword(String email) {

		User existingUser = userRepository.findByEmailIdIgnoreCase(email);
		AuthenticationToken previousToken = authenticationTokenRepo.findByUser(existingUser);
		authenticationTokenRepo.delete(previousToken);
		String token = TokenGenerator.generateToken();
		AuthenticationToken authenticationToken = new AuthenticationToken(token, existingUser);
		emailService.mailSend(email, token);
		authenticationToken.setTokentype(TokenType.FORGOT_PASSWORD);
		authenticationTokenRepo.saveAndFlush(authenticationToken);

	}

	@Override
	public User verifyToken(String token) {
		if (token != null && !token.isEmpty()) {
			AuthenticationToken authenticationToken = authenticationTokenRepo.findByToken(token);
			if (authenticationToken != null) {
				User user = authenticationToken.getUser();
				boolean isExpired = authenticationTokenService.isTokenExpired(authenticationToken);
				if (user != null && user.getIsEnabled() == true && isExpired == false) {
					return user;
				}
			}
		}
		return null;
	}

	@Override
	public void resetPassword(User user, ResetPasswordForm resetPasswordForm) {
		user.setPassword(passwordEncoder.encode(resetPasswordForm.getNewPassword()));
		userRepository.save(user);
	}

}
