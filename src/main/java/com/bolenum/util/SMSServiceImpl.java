package com.bolenum.util;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class SMSServiceImpl implements SMSService {

	@Value("${bolenum.twilio.account.SID}")
	String twilioAccountSid;
	@Value("${bolenum.twilio.auth.token}")
	String twilioAuthToken;
	@Value("${bolenum.twilio.phone.number}")
	String twilioMobileNumber;
	@Value("${bolenum.2factor.otp.api.dev}")
	String twoFactorUrl;

	public static final Logger logger = LoggerFactory.getLogger(SMSServiceImpl.class);

	@Override
	@Async
	public void sendMessage(String mobileNumber, String countryCode, String msg) {
		Twilio.init(twilioAccountSid, twilioAuthToken);
		try {
			Message.creator(new PhoneNumber("+" + countryCode + mobileNumber), // to
					new PhoneNumber(twilioMobileNumber), // from
					msg).create();
			logger.info("Message Sent successfully");
		} catch (ApiException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void sendOtp(int otp, String countryCode, String mobileNumber) {
		RestTemplate restTemplate = new RestTemplate();
		String url = twoFactorUrl + "+" + countryCode + mobileNumber + "/" + otp + "/bolenum otp";
		restTemplate.getForObject(url, HashMap.class);
	}
}
