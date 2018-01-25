package com.bolenum.enums;

/**
 * 
 * @Author Himanshu Kumar
 * @Date 03-Nov-2017
 */

public enum TransactionStatus {

	WITHDRAW("withdraw"), DEPOSIT("deposit"), FEE("fee"), TRANSFER("transfer");

	private String txStatus;

	private TransactionStatus(String txType) {
		this.txStatus = txType;
	}

	/**
	 * This method is use to get transaction status.
	 * @param Nothing
	 * @return txStatus
	 */
	public String getTransactionStatus() {
		return txStatus;
	}

}
