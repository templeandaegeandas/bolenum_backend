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
	/**
	 * @return the receiver
	 */
	public Long getReceiver() {
		return receiver;
	}
	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(Long receiver) {
		this.receiver = receiver;
	}
	/**
	 * @return the messageType
	 */
	public MessageType getMessageType() {
		return messageType;
	}
	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	
	
}
