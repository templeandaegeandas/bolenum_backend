package com.bolenum.enums;

public enum DocumentStatus {
	SUBMITTED("submitted"),APPROVED("approved"),DISAPPROVED("disapproved");
	private String status;

	private DocumentStatus(String documentStatus) {
			this.status = documentStatus;
		}

	/**
	 * This method is use to get document status.
	 * @param Nothing
	 * @return status
	 */	
	public String getDocumentStatus() {
		return status;
	}
}