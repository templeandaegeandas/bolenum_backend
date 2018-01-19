/*@Description Of interface
 * 
 * UserService interface is responsible for below listed task: 
 *   
 *     Save user
 *     Find by email
 *     Re Register
 *     Change password
 *     Update user profile
 *     Add mobile number
 *     Re send OTP
 *     Verify OTP
 *     Find by user id
 *     Upload image
 *     KYC Verified
 *     Save user coin
 **/

package com.bolenum.services.user;

import java.io.IOException;

import com.bolenum.dto.common.EditUserForm;
import com.bolenum.dto.common.PasswordForm;
import com.bolenum.dto.common.UserSignupForm;
import com.bolenum.exceptions.InvalidOtpException;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.User;
import com.bolenum.model.coin.UserCoin;

/**
 * 
 * @Author Himanshu
 * @Date 11-Sep-2017
 */

public interface UserService {
	
	/**@description use to register user
	 * @param user
	 */
	public void registerUser(User user);
	
	
	/**@description use to save user
	 * @param       user
	 * @return      user
	 */
	public User saveUser(User user);

	/**@description use to find user by email
	 * @param       email
	 * @return      user
	 */
	public User findByEmail(String email);
	
	
	/**@description use to re register user
	 * @param signupForm
	 */
	public void reRegister(UserSignupForm signupForm);

	/**
	 * @description use to change password
	 * @param user
	 * @param passwordForm
	 * @return Boolean
	 * @throws InvalidPasswordException
	 */
	Boolean changePassword(User user, PasswordForm passwordForm);
	
	
	/**
	 * @description use to update user profile
	 * @param editUserForm
	 * @param user
	 * @return Boolean
	 * 
	 */
	User updateUserProfile(EditUserForm editUserForm, User user);

	/**@description use to add mobile number
	 * @param mobileNumber
	 * @param user
	 * @return User
	 * @throws PersistenceException
	 */
	User addMobileNumber(String mobileNumber, String countryCode, User user) throws PersistenceException;

	/**
	 * @description use to verifies OTP
	 * @param otp
	 * @param user
	 * @return Boolean
	 * @throws InvalidOtpException
	 */
	Boolean verifyOTP(Integer otp, User user) throws InvalidOtpException;

	/**
	 * @description use to re-send otp 
	 * @param user
	 * 
	 */
	void resendOTP(User user);
	
	/**
	 * @description use to find user by Id
	 * @param  id
	 * @return user
	 * 
	 */
	public User findByUserId(Long id);
	
	
	/**
	 * @description use to upload image of user
	 * @param  id
	 * @return user
	 * @throws IOException
	 * @throws PersistenceException
	 * @throws MaxSizeExceedException
	 */
	public User uploadImage(String imageBase64, Long userId)
			throws IOException, PersistenceException, MaxSizeExceedException;
	/**
	 * @description use to verified KYC
	 * @param  user
	 * @return boolean
	 * 
	 */
	public boolean isKycVerified(User user);

	/**
	 * @description use to save user coin
	 * @param   walletAddress
	 * @param   user
	 * @param   tokenName
	 * @return  user coin
	 */
	UserCoin saveUserCoin(String walletAddress, User user, String tokenName);
}
