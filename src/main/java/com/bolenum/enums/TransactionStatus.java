package com.bolenum.enums;
/**
 * 
 * @Author Himanshu Kumar
 * @Date 03-Nov-2017
 */

public enum TransactionStatus {

WITHDRAW("WITHDRAW"), DEPOSIT("DEPOSIT");
	
	private String txStatus;

	private TransactionStatus(String txType) {
		this.txStatus = txType;
	}

	public String getTransactionStatus() {
		return txStatus;
	}
	
}
