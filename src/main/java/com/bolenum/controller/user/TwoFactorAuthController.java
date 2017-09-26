package com.bolenum.controller.user;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.TwoFactorAuthOption;
import com.bolenum.constant.UrlConstant;
import com.bolenum.exceptions.InvalidOtpException;
import com.bolenum.model.OTP;
import com.bolenum.model.User;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.user.TwoFactorAuthService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;
import com.google.zxing.WriterException;

/**
 * 
 * @author Vishal Kumar
 * @date 26-sep-2017
 *
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
public class TwoFactorAuthController {

	@Autowired
	private TwoFactorAuthService twoFactorAuthService;
	@Autowired
	private LocaleService localeService;
	
	@RequestMapping(value = UrlConstant.GEN_GOOGLE_AUTH_QR, method = RequestMethod.POST)
	ResponseEntity<Object> generateGoogleAuthQr() throws URISyntaxException, WriterException, IOException {
		User user = GenericUtils.getLoggedInUser();
		Map<String, String> response = twoFactorAuthService.qrCodeGeneration(user);
		if (response != null) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("tfa.qr.code.generation.success"), response);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("tfa.qr.code.generation.failure"), null);
		}
	}

	@RequestMapping(value = UrlConstant.VERIFY_GOOGLE_AUTH_KEY, method = RequestMethod.PUT)
	ResponseEntity<Object> authenticateGoogleAuthKey(@RequestParam String secret) {
		User user = GenericUtils.getLoggedInUser();
		boolean authResponse = twoFactorAuthService.performAuthentication(secret, user);
		if (authResponse) {
			User updateResponse = twoFactorAuthService.setTwoFactorAuth(TwoFactorAuthOption.GOOGLE_AUTHENTICATOR,
					user);
			if (updateResponse != null) {
				return ResponseHandler.response(HttpStatus.OK, false,
						localeService.getMessage("tfa.set.to.google.authenticator.success"), null);
			} else {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localeService.getMessage("tfa.set.to.google.authenticator.failure"), null);
			}
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("tfa.set.to.google.authenticator.failure"), null);
		}
	}

	@RequestMapping(value = UrlConstant.TWO_FACTOR_AUTH_VIA_MOBILE, method = RequestMethod.PUT)
	ResponseEntity<Object> setTwoFactorAuthViaMobile() {
		User user = GenericUtils.getLoggedInUser();
		if (user.getMobileNumber() != null) {
			if (user.getIsMobileVerified()) {
				User updateResponse = twoFactorAuthService.setTwoFactorAuth(TwoFactorAuthOption.MOBILE, user);
				if (updateResponse != null) {
					return ResponseHandler.response(HttpStatus.OK, false,
							localeService.getMessage("tfa.set.to.via.mobile.success"), null);
				} else {
					return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
							localeService.getMessage("tfa.set.to.via.mobile.failure"), null);
				}
			} else {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localeService.getMessage("tfa.please.verify.your.mobile"), null);
			}
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("tfa.please.add.mobile.number"), null);
		}
	}

	@RequestMapping(value = UrlConstant.SEND_2FA_OTP, method = RequestMethod.PUT)
	ResponseEntity<Object> sendOtpForTwoFactorAuth() {
		User user = GenericUtils.getLoggedInUser();
		OTP otp = twoFactorAuthService.sendOtpForTwoFactorAuth(user);
		if (otp != null) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("tfa.otp.send.successfully"),
					null);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("tfa.unable.to.send.otp"), null);
		}
	}

	@RequestMapping(value = UrlConstant.VERIFY_2FA_OTP, method = RequestMethod.PUT)
	ResponseEntity<Object> verify2faOtp(@RequestParam("otp") int otp) throws InvalidOtpException {
		boolean response = twoFactorAuthService.verify2faOtp(otp);
		if (response) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("tfa.otp.verification.successful"), response);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("tfa.otp.verification.failure"), null);
		}
	}

	@RequestMapping(value = UrlConstant.REMOVE_TWO_FACTOR_AUTH, method = RequestMethod.DELETE)
	ResponseEntity<Object> removeTwoFactorAuth() {
		User user = GenericUtils.getLoggedInUser();
		User updateResponse = twoFactorAuthService.setTwoFactorAuth(TwoFactorAuthOption.NONE, user);
		if (updateResponse != null) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("tfa.removed.success"),
					updateResponse);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("tfa.remove.failure"), null);
		}
	}

}
