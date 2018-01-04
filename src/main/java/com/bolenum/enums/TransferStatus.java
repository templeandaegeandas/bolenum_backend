package com.bolenum.enums;

public enum TransferStatus {

	INITIATED("initiated"), PROCESSING("processing"), PENDING("pending"), COMPLETED("completed");

	private String status;

	private TransferStatus(String txType) {
		this.status = txType;
	}

	public String getTransferStatus() {
		return status;
	}
}
