package com.bolenum.util;

public interface SMSService {

	void sendMessage(String mobileNumber, String countryCode, String msg);

	void sendOtp(int otp, String countryCode, String mobileNumber) throws Exception;

}
