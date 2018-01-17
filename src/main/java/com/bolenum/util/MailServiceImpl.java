package com.bolenum.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

	@Autowired
	private JavaMailSender emailSender;

	@Value("${bolenum.url}")
	private String serverUrl;

	@Value("${bolenum.mail.from}")
	private String mailFrom;

	@Override
	public void registrationMailSend(String to, String token) {

		// SimpleMailMessage message = new SimpleMailMessage();
		
		MimeMessage message = emailSender.createMimeMessage();
		try {
			
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("url", serverUrl + "/#/login?token=" + token);
			model.put("to", to);
			helper.setTo(to);
			String html = ThymeleafUtil.getProcessedHtml("emailTemplate", model);
			helper.setText(html, true);
			helper.setSubject("Verification link for registration");
			helper.setFrom(new InternetAddress(mailFrom, "Bolenum Exchange").toString());
			emailSender.send(message);
		} catch (UnsupportedEncodingException e) {
			logger.error("registrationMailSend exce: {}", e);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
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