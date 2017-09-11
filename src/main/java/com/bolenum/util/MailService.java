package com.bolenum.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.stereotype.Service;

import com.alchemistcoin.model.User;

@Service
public class MailService 
{
	
	  @Autowired
	  private MailSender mailSender;
	 
	  public void mailSend(User user,String token)
	  {
	       SimpleMailMessage message = new SimpleMailMessage();
	       message.setSubject("verification link for Reistration");
           message.setText("please verify by clicking on link http://localhost:8080/api/v1/user/verify?token="+token);
           message.setTo(user.getEmailId());
	       mailSender.send(message);
	     
	  }
	  
}