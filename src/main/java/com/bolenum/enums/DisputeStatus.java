package com.bolenum.enums;

public enum DisputeStatus {


	RAISED("raised"), COMPLETED("completed"), INPROCESS("inprocess"), CANCELLED("cancelled");

	private String dispute;

	private DisputeStatus(String dispute) {
		this.dispute = dispute;
	}

	public String getDocumentType() {
		return dispute;
	}
}
