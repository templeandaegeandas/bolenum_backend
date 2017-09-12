package com.bolenum.services.user;

import org.joda.time.DateTime;
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

/**
 * 
 * @Author Himanshu
 * @Date 12-Sep-2017
 */
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
		user.setCreatedOn(new DateTime());
		user.setIsEnabled(false);
		userRepository.save(user);
		sendToken(user);
	}

	public void sendToken(User user) {
		String token = TokenGenerator.generateToken();
		DateTime date = new DateTime();
		VerificationToken verificationToken = new VerificationToken(token, user);
		verificationToken.setTokentype(TokenType.REGISTRATION);
		verificationToken.setCreatedOn(date);
		tokenRepository.saveAndFlush(verificationToken);
		emailservice.registrationMailSend(user.getEmailId(), token);
	}

	public void sendTokenIfUserAlreadyExist(User user) {
		  VerificationToken verificationToken =tokenRepository.findByUser(user);
          verificationToken.setCreatedOn(verificationToken.getCreatedOn().plusHours(2));
          emailservice.registrationMailSend(user.getEmailId(), verificationToken.getToken());
	} 

	@Override
	public boolean verifyUserToken(String token) {
		// check the token is exist in verification_token table

		VerificationToken verificationToken = tokenRepository.findByToken(token);
		User user = verificationToken.getUser();

		boolean isExpired = isTokenExpired(verificationToken);
		// System.out.println("isExpired =" + isExpired);

		if (verificationToken != null && user != null && user.getIsEnabled() == false && isExpired == false) {
			user.setIsEnabled(true);
			userRepository.saveAndFlush(user);
			return true;
		} else {
			return false;
		}

	}

	public boolean isTokenExpired(VerificationToken verificationTokenToCheck) {

		DateTime tokenCreatedTime = verificationTokenToCheck.getCreatedOn();
		DateTime expirationDate = tokenCreatedTime.plusHours(2);

		if (expirationDate.isBeforeNow()) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public User findByEmail(User user) {
		return userRepository.findByEmailId(user.getEmailId());
	}

}
