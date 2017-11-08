package com.bolenum.enums;

public enum DocumentStatus {
	SUBMITTED("submitted"),APPROVED("approved"),DISAPPROVED("disapproved");
	private String documentStatus;

	private DocumentStatus(String documentStatus) {
			this.documentStatus = documentStatus;
		}

	public String getDocumentStatus() {
		return documentStatus;
	}
}