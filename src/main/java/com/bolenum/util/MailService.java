package com.bolenum.util;

import java.util.concurrent.Future;

public interface MailService {

	Future<Boolean> mailSend(String to, String subject, String text);

	void registrationMailSend(String to, String token);

}
