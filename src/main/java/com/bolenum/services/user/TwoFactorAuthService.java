/*@Description Of interface
 * 
 * TwoFactorAuthService interface is responsible for below listed task: 
 *   
 *     QR code generation
 *     Perform authentication
 *     Set two factor authentication
 *     Send otp for two factor authentication
 **/
package com.bolenum.services.user;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bolenum.enums.TwoFactorAuthOption;
import com.bolenum.exceptions.InvalidOtpException;
import com.bolenum.model.OTP;
import com.bolenum.model.User;
import com.google.zxing.WriterException;

public interface TwoFactorAuthService {

	/**
	 * @description use to generate QR code
	 * @param user
	 * @return Map
	 * @throws URISyntaxException
	 * @throws WriterException
	 * @throws IOException
	 * 
	 */
	Map<String, String> qrCodeGeneration(User user) throws URISyntaxException, WriterException, IOException;

	/**
	 * @description Use to perform authentication for user
	 * @param value
	 * @param user
	 * @return Boolean
	 * @throws UsernameNotFoundException
	 */
	boolean performAuthentication(String value, User user) throws UsernameNotFoundException;

	/**
	 * @description use to set two factor authentication 
	 * @param twoFactorAuthOption
	 * @param user
	 * @return User
	 */
	User setTwoFactorAuth(TwoFactorAuthOption twoFactorAuthOption, User user);

	/**
	 * @description Use to send otp for two factor authentication user
	 * @param user
	 * @return OTP
	 * @throws Exception
	 */
	OTP sendOtpForTwoFactorAuth(User user);

	/**
	 * @description use to verify two factor OTP
	 * @param otp
	 * @return Boolean
	 * @throws InvalidOtpException
	 * 
	 */
	boolean verify2faOtp(int otp) throws InvalidOtpException;
}
