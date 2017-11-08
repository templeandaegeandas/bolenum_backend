package com.bolenum.enums;
/**
 * 
 * @Author Himanshu Kumar
 * @Date 03-Nov-2017
 */

public enum TransactionStatus {

WITHDRAW("withdraw"), DEPOSIT("deposit");

	private String txStatus;

	private TransactionStatus(String txType) {
		this.txStatus = txType;
	}

	public String getTransactionStatus() {
		return txStatus;
	}
	
}
