package com.bolenum.services.user;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.bolenum.constant.TokenType;
import com.bolenum.dao.user.TokenRepository;
import com.bolenum.dao.user.UserRepository;
import com.bolenum.model.User;
import com.bolenum.model.VerificationToken;
import com.bolenum.services.user.UserService;
import com.bolenum.util.MailService;
import com.bolenum.util.TokenGenerator;

public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private MailService emailservice;

	@Override
	public void RegisterUser(User user) {
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

	private void sendToken(User user) {
		String token = TokenGenerator.generateToken();
		Date date = new Date();
		emailservice.mailSend(user, token);
		VerificationToken verificationToken = new VerificationToken(token, user);
		verificationToken.setTokentype(TokenType.REGISTRATION);
		verificationToken.setCreatedOn(new Timestamp(date.getTime()));
		tokenRepository.saveAndFlush(verificationToken);
	}

	@Override
	public boolean verifyUserToken(String token) {
		return false;
	}

	@Override
	public Boolean userIsExist(User user) {
		return false;
	}

	@Override
	public boolean userIsAlreadyRegistered(User user) {
		// TODO Auto-generated method stub
		return false;
	}

}
