package com.bolenum.util;

public interface SMSService {

	void sendMessage(String mobileNumber, String countryCode, String msg);

}
