package com.bolenum.util;

public interface MailService {

	boolean mailSend(String to, String subject, String text);

	void registrationMailSend(String to, String token);

}
