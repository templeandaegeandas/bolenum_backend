package com.bolenum.util;

import java.util.Map;
import java.util.concurrent.Future;

public interface MailService {


	/**
	 * This method is use for mail Send
	 * @param to
	 * @param subject
	 * @param text
	 * @return
	 */
	Future<Boolean> mailSend(String to, String subject, String text);

	/**
	 * This method is use for registration Mail Send
	 * @param to
	 * @param token
	 */
	void registrationMailSend(String to, String token) ;


	Future<Boolean> mailSend(String to, String subject, Map<String, Object> map, String emailTemplate);

}
