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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.bolenum.constant.EmailTemplate;

/**
 * 
 * @Author Himanshu
 * @Date 13-Sep-2017
 */
@Service
public class MailServiceImpl implements MailService {
	public static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

	@Autowired
	private JavaMailSender emailSender;

	@Value("${bolenum.url}")
	private String serverUrl;

	@Value("${bolenum.mail.from}")
	private String mailFrom;

	@Value("${bolenum.domain.name}")
	private String domainName;

	@Override
	public void registrationMailSend(String to, String token) {

		MimeMessage message = emailSender.createMimeMessage();
		try {

			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			Map<String, Object> model = new HashMap<>();
			model.put("url", serverUrl + "/#/login?token=" + token);
			model.put("to", to);
			helper.setTo(to);
			String html = ThymeleafUtil.getProcessedHtml(model, EmailTemplate.REGISTRATION_TEMPLATE);
			helper.setText(html, true);
			helper.setSubject("Verification link for registration");
			helper.setFrom(new InternetAddress(mailFrom, domainName).toString());
			emailSender.send(message);
		} catch (UnsupportedEncodingException | MessagingException e) {
			logger.error("registrationMailSend exce: {}", e);

		}

	}

	/**
	 * 
	 * @modify by Himanshu Kumar
	 * 
	 */
	@Override
	@Async
	public Future<Boolean> mailSend(String to, String subject, Map<String, Object> map, String emailTemplate) {
		MimeMessage message = emailSender.createMimeMessage();
		try {

			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			helper.setTo(to);
			String html = ThymeleafUtil.getProcessedHtml(map, emailTemplate);
			helper.setText(html, true);
			helper.setSubject(subject);
			helper.setFrom(new InternetAddress(mailFrom, domainName).toString());
			emailSender.send(message);
		} catch (UnsupportedEncodingException | MessagingException e) {
			logger.error("registrationMailSend exce: {}", e);
		}
		return new AsyncResult<>(false);
	}

	@Override
	public Future<Boolean> mailSend(String to, String subject, String text) {
		// TODO Auto-generated method stub
		return null;
	}
}