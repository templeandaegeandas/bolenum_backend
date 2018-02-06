package com.bolenum.enums;

public enum DisputeStatus {

	RAISED("raised"), COMPLETED("completed"), INPROCESS("inprocess"), CANCELLED("cancelled");
	private String status;

	private DisputeStatus(String disputeStatus) {
		this.status = disputeStatus;
	}

	/**
	 * This method is use to get document type.
	 * @param Nothing
	 * @return status
	 */
	public String getDocumentType() {
		return status;
	}
}
