package com.bolenum.controller.web.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.bolenum.constant.UrlConstant;
import com.bolenum.model.web.socket.WebSocketMessage;

/**
 * 
 * @author Vishal Kumar
 * @date 07-Nov-2017
 *
 */

@Controller
public class WebSocketController {
	
	public static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping(UrlConstant.WS_SENDER_USER)
	void sendMesageToUser(WebSocketMessage webSocketMessage) {
		logger.debug("Message type: {}",webSocketMessage.getMessageType());
		simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_ADMIN,
				webSocketMessage.getMessageType().toString());
	}

	@MessageMapping(UrlConstant.WS_SENDER_ADMIN)
	void sendMesageToAdmin(WebSocketMessage webSocketMessage) {
		logger.debug("Message type: {}",webSocketMessage.getMessageType().toString());
		simpMessagingTemplate.convertAndSend(
				UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + webSocketMessage.getReceiver(),
				webSocketMessage.getMessageType());
	}

}
