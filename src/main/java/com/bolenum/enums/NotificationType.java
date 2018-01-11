package com.bolenum.enums;

/**
 * 
 * @author Vishal Kumar
 * @date 11-Jan-2018
 *
 */
public enum NotificationType {

	PAID_NOTIFICATION("paid_notification"), MATCHED_NOTIFICATION("matched_notification"), KYC_NOTIFICATION("kyc_notification"), DISPUTE_NOTIFICATION("dispute_notification");
	
	private String notification;
	
	private NotificationType(String notification) {
		this.notification = notification;
	}
	
	public String getNotificationType() {
		return notification;
	}
}
