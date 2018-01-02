package com.bolenum.enums;

public enum MessageType {
	DOCUMENT_VERIFICATION("document_verification"), USER_NOTIFICATION("user_notification"), ADMIN_NOTIFICATION(
			"admin_notification"), ORDER_BOOK_NOTIFICATION("order_book_notification"), DEPOSIT_NOTIFICATION(
					"deposite_notification"), WITHDRAW_NOTIFICATION("withdraw_notification"), ORDER_CONFIRMATION(
							"order_confirmation"), ORDER_CANCELLED("order_cancelled"), PAID_NOTIFICATION(
									"paid_notification"), MATCHED_NOTIFICATION("matched_notification"), MARKET_UPDATE("market_update");

	private String messageType;

	private MessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageType() {
		return messageType;
	}
}
