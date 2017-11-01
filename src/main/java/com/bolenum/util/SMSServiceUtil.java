package com.bolenum.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


@EnableAsync
@Service
public class SMSServiceUtil {
	
	@Value("${bolenum.twilio.account.SID}")
	String twilioAccountSid;
	@Value("${bolenum.twilio.auth.token}")
	String twilioAuthToken;
	@Value("${bolenum.twilio.phone.number}")
	String twilioMobileNumber;
	
	public static final Logger logger = LoggerFactory.getLogger(SMSServiceUtil.class);

	public void sendMessage(String mobileNumber, String countryCode, String msg) {
		Twilio.init(twilioAccountSid, twilioAuthToken);
		try {
			Message.creator(new PhoneNumber("+" + countryCode+ mobileNumber.toString()), // to
					new PhoneNumber(twilioMobileNumber), // from
					msg).create();
			logger.info("Message Sent successfully");
		} catch (ApiException e) {
			logger.error(e.getMessage());
		}
	}
}
