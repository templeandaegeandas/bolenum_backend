package com.bolenum.util;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

/**
 * 
 * @Author Himanshu
 * @Date 13-Sep-2017
 */
@Service
public class MailServiceImpl implements MailService{
	public static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

	@Autowired
	private MailSender mailSender;

	@Value("${bolenum.url}")
	private String serverUrl;
	
	@Value("${bolenum.mail.from}")
	private String mailFrom;

	@Override
	public void registrationMailSend(String to, String token) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject("verification link for Reistration");
		message.setText("please verify by clicking on link " + serverUrl + "/#/login?token=" + token);
		message.setTo(to);
		message.setFrom(mailFrom);
		mailSender.send(message);
	}

	@Override
	@Async
	public Future<Boolean> mailSend(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(subject);
		message.setText(text);
		message.setFrom(mailFrom);
		message.setTo(to);
		try {
			mailSender.send(message);
			logger.debug("mail sent succssfully to: {}", to);
			return new AsyncResult<Boolean>(true);
		} catch ( MailException e) {
			logger.error("mail seding failed to: {}", to);
			e.printStackTrace();
		}
		return new AsyncResult<Boolean>(false);
	}
}