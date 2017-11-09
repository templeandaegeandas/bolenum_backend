package com.bolenum.enums;

public enum MessageType {
	DOCUMENT_VERIFICATION("document_verification"), USER_NOTIFICATION("user_notification"), ADMIN_NOTIFICATION(
			"admin_notification"), ORDER_BOOK_NOTIFICATION("order_book_notification"),DEPOSIT_NOTIFICATION("deposite_notification");
	
	private String messageType;

	private MessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageType() {
		return messageType;
	}
}
