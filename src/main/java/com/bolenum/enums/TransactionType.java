/**
 * 
 */
package com.bolenum.enums;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
public enum TransactionType {
	INCOMING("incoming"), OUTGOING("outgoing");
	
	private String txType;

	private TransactionType(String txType) {
		this.txType = txType;
	}

	public String getTransactionType() {
		return txType;
	}
	
}
