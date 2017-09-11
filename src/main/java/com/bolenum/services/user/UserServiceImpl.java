package com.bolenum.services.user;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.constant.TokenType;
import com.bolenum.dao.user.TokenRepository;
import com.bolenum.dao.user.UserRepository;
import com.bolenum.model.User;
import com.bolenum.model.VerificationToken;
import com.bolenum.services.user.UserService;
import com.bolenum.util.MailService;
import com.bolenum.util.TokenGenerator;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private MailService emailservice;

	@Override
	public void registerUser(User user) {
		saveUser(user);
		sendToken(user);
	}

	@Override
	public void saveUser(User user) {
		Date date = new Date();
		user.setCreatedOn(new Timestamp(date.getTime()));
		user.setIsEnabled(false);
		userRepository.save(user);
	}

	public void sendToken(User user) {
		String token = TokenGenerator.generateToken();
		Date date = new Date();
		emailservice.mailSend(user, token);
		VerificationToken verificationToken = new VerificationToken(token, user);
		verificationToken.setTokentype(TokenType.REGISTRATION);
		verificationToken.setCreatedOn(new Timestamp(date.getTime()));
		tokenRepository.saveAndFlush(verificationToken);
	}

	@Override
	public boolean isUserExist(User user) {
		User existingUser = userRepository.findByEmailId(user.getEmailId());

		if (existingUser != null && existingUser.getEmailId().equals(user.getEmailId())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isUserAlreadyRegistered(User user) {
		User existingUser = userRepository.findByEmailId(user.getEmailId());
		if (existingUser.getIsEnabled() == true) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean verifyUserToken(String token) {
		// check the token is exist in verification_token table

		VerificationToken verificationToken = tokenRepository.findByToken(token);
		User user = verificationToken.getUser();

		boolean isExpired = isTokenExpired(verificationToken);
		System.out.println("isExpired =" + isExpired);

		if (verificationToken != null && user != null && user.getIsEnabled() == false && isExpired == false) {
			user.setIsEnabled(true);
			userRepository.saveAndFlush(user);
			return true;
		} else {
			return false;
		}

	}

	public boolean isTokenExpired(VerificationToken verificationTokenToCheck) {
		Date date = new Date();
		Timestamp currentTimeStamp = new Timestamp(date.getTime());
		Long currentTime = currentTimeStamp.getTime();

		Timestamp timestampToCheckExpiry = verificationTokenToCheck.getCreatedOn();
		timestampToCheckExpiry.setTime(timestampToCheckExpiry.getTime() + (((14 * 60) + 59) * 1000));
		Long tokenExpiry = timestampToCheckExpiry.getTime();

		if (currentTime > tokenExpiry) {
			return true;
		} else {
			return false;
		}
	}

}
