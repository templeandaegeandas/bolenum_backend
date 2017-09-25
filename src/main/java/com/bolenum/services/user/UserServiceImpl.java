package com.bolenum.services.user;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.constant.TokenType;
import com.bolenum.dto.common.EditUserForm;
import com.bolenum.dto.common.PasswordForm;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;
import com.bolenum.repo.common.AuthenticationTokenRepo;
import com.bolenum.repo.common.RoleRepo;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.services.common.LocaleService;
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
	private AuthenticationTokenRepo authenticationTokenRepo;

	@Autowired
	private MailService emailservice;

	@Autowired
	private PasswordEncoderUtil passwordEncoder;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private LocaleService localService;

	/**
	 * to register user if and only if when user details not present in database
	 */
	@Override
	public void registerUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(roleRepo.findByNameIgnoreCase("ROLE_USER"));
		userRepository.save(user);
		AuthenticationToken authenticationToken = mailVerification(user);
		authenticationToken.setTokentype(TokenType.REGISTRATION);
		authenticationTokenRepo.saveAndFlush(authenticationToken);

	}

	/**
	 * to send mail which contains verification link and Authentication token
	 * 
	 * @param user
	 * @return
	 */
	private AuthenticationToken mailVerification(User user) {
		String token = TokenGenerator.generateToken();
		AuthenticationToken authenticationToken = new AuthenticationToken(token, user);
		emailservice.registrationMailSend(user.getEmailId(), token);
		return authenticationToken;
	}

	/**
	 * find user with respect to email id
	 */
	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmailIdIgnoreCase(email);
	}

	/**
	 * to save user details
	 */
	@Override
	public User saveUser(User user) {
		return userRepository.saveAndFlush(user);
	}

	/**
	 * 
	 * to re register user if already details present in user table
	 * 
	 */
	@Override
	public void reRegister(User isUserExist) {

		List<AuthenticationToken> verificationToken = authenticationTokenRepo.findByUserAndTokentype(isUserExist,
				TokenType.REGISTRATION);

		for (AuthenticationToken token : verificationToken) {
			if (token.getTokentype() == TokenType.REGISTRATION) {
				authenticationTokenRepo.delete(token);
			}
		}
		registerUser(isUserExist);

	}

	/**
	 * to change password
	 */
	@Override
	public boolean changePassword(User user, PasswordForm passwordForm) throws InvalidPasswordException {
		if (passwordEncoder.matches(passwordForm.getOldPassword(), user.getPassword())) {
			user.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
			userRepository.save(user);
			return true;
		} else {
			throw new InvalidPasswordException(localService.getMessage("invalid.credential"));
		}
	}

	/**
	 * to update user profile
	 */

	@Override
	public User updateUserProfile(EditUserForm editUserForm, User user) {

		if (editUserForm.getFirstName() != null) {
			user.setFirstName(editUserForm.getFirstName());
		}

		if (editUserForm.getMiddleName() != null) {
			user.setMiddleName(editUserForm.getMiddleName());
		}

		if (editUserForm.getLastName() != null) {
			user.setLastName(editUserForm.getLastName());
		}

		if (editUserForm.getAddress() != null) {
			user.setAddress(editUserForm.getAddress());
		}

		if (editUserForm.getCity() != null) {
			user.setCity(editUserForm.getCity());
		}

		if (editUserForm.getState() != null) {
			user.setState(editUserForm.getState());
		}

		if (editUserForm.getCountry() != null) {
			user.setCountry(editUserForm.getCountry());
		}

		if (editUserForm.getMobileNumber() != null) {
			user.setMobileNumber(editUserForm.getMobileNumber());
		}

		if (editUserForm.getGender() != null) {
			user.setGender(editUserForm.getGender());
		}

		if (editUserForm.getDob() != null) {
			user.setDob(editUserForm.getDob());
		}

		return userRepository.saveAndFlush(user);

	}

	/**
	 * find user with respect to id
	 */
	@Override
	public User findByUserId(Long id) {
		return userRepository.findByUserId();

	}

}
