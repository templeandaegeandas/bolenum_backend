package com.bolenum.util;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;

import javax.mail.internet.InternetAddress;

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
public class MailServiceImpl implements MailService {
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
		message.setSubject("Verification link for registration");
		message.setText("Please verify by clicking on link " + serverUrl + "/#/login?token=" + token);
		message.setTo(to);
		try {
			message.setFrom(new InternetAddress(mailFrom, "Bolenum Exchange").toString());
		} catch (UnsupportedEncodingException e) {
			logger.error("registrationMailSend exce: {}", e);
		}
		mailSender.send(message);
	}

	@Override
	@Async
	public Future<Boolean> mailSend(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(subject);
		message.setText(text);
		try {
			message.setFrom(new InternetAddress(mailFrom, "Bolenum Exchange").toString());
			message.setTo(to);
			mailSender.send(message);
			logger.debug("mail sent succssfully to: {}", to);
			return new AsyncResult<>(true);
		} catch (UnsupportedEncodingException | MailException e) {
			logger.error("mail seding failed to: {} {}", to, e);
		}
		return new AsyncResult<>(false);
	}
}