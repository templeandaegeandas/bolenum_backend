package com.bolenum.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	public static final Logger logger = LoggerFactory.getLogger(MailService.class);

	@Autowired
	private MailSender mailSender;
	
	@Value("${bolenum.url}")
	private String serverUrl;

	public void registrationMailSend(String to, String token) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject("verification link for Reistration");
		message.setText("please verify by clicking on link http://" + serverUrl + "/api/v1/user/verify?token=" + token);
		message.setTo(to);
		mailSender.send(message);
	}

	public void mailSend(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(subject);
		message.setText(body);
		message.setTo(to);
		mailSender.send(message);
	}
}