package com.bolenum.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * 
 * @Author Himanshu
 * @Date 13-Sep-2017
 */
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
		message.setText("please verify by clicking on link " + serverUrl + "/#/login?token=" + token);
		message.setTo(to);
		mailSender.send(message);
	}

	public void mailSend(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(subject);
		message.setText(text);
		message.setTo(to);

		try {
			mailSender.send(message);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}