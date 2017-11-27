package com.bolenum.model.web.socket;

import com.bolenum.enums.MessageType;

/**
 *
 * @author Vishal Kumar
 * @date 07-Nov-2017
 *
 */

public class WebSocketMessage {
	Long receiver;
	MessageType messageType;

	public Long getReceiver() {
		return receiver;
	}

	public void setReceiver(Long receiver) {
		this.receiver = receiver;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	
}
