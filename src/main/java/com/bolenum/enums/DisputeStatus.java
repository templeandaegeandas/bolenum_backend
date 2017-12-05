package com.bolenum.enums;

public enum DisputeStatus {

	RAISED("raised"), COMPLETED("completed"), INPROCESS("inprocess"),CANCELED("canceled");

	private String dispute;

	private DisputeStatus(String dispute) {
		this.dispute = dispute;
	}

	public String getDocumentType() {
		return dispute;
	}
}
