package com.bolenum.services.user;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.constant.Message;
import com.bolenum.constant.TokenType;
import com.bolenum.dto.common.PasswordForm;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;
import com.bolenum.repo.common.AuthenticationTokenRepo;
import com.bolenum.repo.common.RoleRepo;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.util.MailService;
import com.bolenum.util.PasswordEncoderUtil;
import com.bolenum.util.TokenGenerator;

/**
 * 
 * @Author Himanshu
 * @Date 12-Sep-2017
 */
@Transactional
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationTokenRepo tokenRepository;

	@Autowired
	private MailService emailservice;

	@Autowired
	private PasswordEncoderUtil passwordEncoder;

	@Autowired
	private RoleRepo roleRepo;

	@Override
	public void registerUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(roleRepo.findByNameIgnoreCase("ROLE_USER"));
		userRepository.save(user);
		AuthenticationToken authenticationToken = mailVerification(user);
		authenticationToken.setTokentype(TokenType.REGISTRATION);
		tokenRepository.saveAndFlush(authenticationToken);

	}

	private AuthenticationToken mailVerification(User user) {
		String token = TokenGenerator.generateToken();
		AuthenticationToken authenticationToken = new AuthenticationToken(token, user);
		emailservice.registrationMailSend(user.getEmailId(), token);
		return authenticationToken;
	}

	@Override
	public boolean verifyUserToken(String token) {

		if (token != null && !token.isEmpty()) {
			AuthenticationToken authenticationToken = tokenRepository.findByToken(token);
			if (authenticationToken != null) {
				User user = authenticationToken.getUser();
				boolean isExpired = isTokenExpired(authenticationToken);
				if (user != null && user.getIsEnabled() == false && isExpired == false) {
					user.setIsEnabled(true);
					userRepository.saveAndFlush(user);
					return true;
				}
			}
		}
		return false;
	}

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

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmailIdIgnoreCase(email);
	}

	@Override
	public User saveUser(User user) {
		return userRepository.saveAndFlush(user);
	}

	@Override
	public void reRegister(User isUserExist) {
		AuthenticationToken verificationToken = tokenRepository.findByUser(isUserExist);
		tokenRepository.delete(verificationToken);
		registerUser(isUserExist);

	}

	@Override
	public boolean changePassword(User user, PasswordForm passwordForm) throws InvalidPasswordException {
		if (passwordEncoder.matches(passwordForm.getOldPassword(), user.getPassword())) {
			user.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
			userRepository.save(user);
			return true;
		} else {
			throw new InvalidPasswordException(Message.INVALID_CRED);
		}
	}

}
