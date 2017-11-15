package com.bolenum.services.user;

import java.io.IOException;

import com.bolenum.dto.common.EditUserForm;
import com.bolenum.dto.common.PasswordForm;
import com.bolenum.exceptions.InvalidOtpException;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.User;

/**
 * 
 * @Author Himanshu
 * @Date 11-Sep-2017
 */

public interface UserService {
	public void registerUser(User user);

	public User saveUser(User user);

	public User findByEmail(String email);

	public void reRegister(User isUserExist);

	/**
	 * 
	 * @param user
	 * @param passwordForm
	 * @return Boolean
	 * @throws InvalidPasswordException
	 */
	Boolean changePassword(User user, PasswordForm passwordForm) throws InvalidPasswordException;

	User updateUserProfile(EditUserForm EditUserForm, User user);

	/**
	 * @param mobileNumber
	 * @param user
	 * @return User
	 */
	User addMobileNumber(String mobileNumber, String countryCode, User user) throws PersistenceException;

	/**
	 * 
	 * @param otp
	 * @param user
	 * @return Boolean
	 * @throws InvalidOtpException
	 */
	Boolean verifyOTP(Integer otp, User user) throws InvalidOtpException;

	/**
	 * 
	 * @param user
	 * @throws Exception
	 */
	void resendOTP(User user);
	public User findByUserId(Long id);
	
	public User uploadImage(String imageBase64, Long userId) throws IOException, PersistenceException, MaxSizeExceedException;
	
	
}
