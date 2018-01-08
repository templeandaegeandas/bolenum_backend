package com.bolenum.enums;

public enum DocumentStatus {
	SUBMITTED("submitted"),APPROVED("approved"),DISAPPROVED("disapproved");
	private String status;

	private DocumentStatus(String documentStatus) {
			this.status = documentStatus;
		}

	public String getDocumentStatus() {
		return status;
	}
}