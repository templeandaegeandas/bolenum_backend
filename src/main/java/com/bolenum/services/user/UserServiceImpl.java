package com.bolenum.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.constant.TokenType;
import com.bolenum.model.User;
import com.bolenum.model.AuthenticationToken;
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
		mailVerification(user);
	}

	private void mailVerification(User user) {
		String token = TokenGenerator.generateToken();
		AuthenticationToken authenticationToken = new AuthenticationToken(token, user);
		authenticationToken.setTokentype(TokenType.REGISTRATION);
		tokenRepository.saveAndFlush(authenticationToken);
		emailservice.registrationMailSend(user.getEmailId(), token);
	}

	public void sendTokenIfUserAlreadyExist(User user) {
		// VerificationToken verificationToken = tokenRepository.findByUser(user);
		// verificationToken.setCreatedOn(verificationToken.getCreatedOn().plusHours(2));
		// emailservice.registrationMailSend(user.getEmailId(),
		// verificationToken.getToken());
	}

	@Override
	public boolean verifyUserToken(String token) {
		// check the token is exist in verification_token table

		// VerificationToken verificationToken = tokenRepository.findByToken(token);
		// User user = verificationToken.getUser();
		//
		// boolean isExpired = isTokenExpired(verificationToken);
		// // System.out.println("isExpired =" + isExpired);
		//
		// if (verificationToken != null && user != null && user.getIsEnabled() == false
		// && isExpired == false) {
		// user.setIsEnabled(true);
		// userRepository.saveAndFlush(user);
		// return true;
		// } else {
		// return false;
		// }
		return false;
	}

	// public boolean isTokenExpired(VerificationToken verificationTokenToCheck) {

	// DateTime tokenCreatedTime = verificationTokenToCheck.getCreatedOn();
	// DateTime expirationDate = tokenCreatedTime.plusHours(2);
	//
	// if (expirationDate.isBeforeNow()) {
	// return false;
	// } else {
	// return true;
	// }

	// }

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
        AuthenticationToken verificationToken=tokenRepository.findByUser(isUserExist);
        tokenRepository.delete(verificationToken);
        registerUser(isUserExist);
        
	}

}
