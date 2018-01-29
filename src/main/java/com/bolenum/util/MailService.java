package com.bolenum.util;

import java.util.Map;
import java.util.concurrent.Future;

import com.bolenum.constant.EmailTemplate;
import com.bolenum.enums.NotificationType;

public interface MailService {

	//Future<Boolean> mailSend(String to, String subject, Map<String, Object> map);

	void registrationMailSend(String to, String token) ;

	//Future<Boolean> mailSend(String to, String subject, Map<String, Object> map, EmailTemplate emailTemplate);

	Future<Boolean> mailSend(String to, String subject, Map<String, Object> map, String emailTemplate);

}
