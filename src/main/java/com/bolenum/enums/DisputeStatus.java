package com.bolenum.enums;

public enum DisputeStatus {

	RAISED("raised"), COMPLETED("completed"), INPROCESS("inprocess"), CANCELLED("cancelled");

	private String disputeStatus;

	private DisputeStatus(String disputeStatus) {
		this.disputeStatus = disputeStatus;
	}

	public String getDocumentType() {
		return disputeStatus;
	}
}