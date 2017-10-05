/**
 * 
 */
package com.bolenum.enums;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
public enum TransactionType {
	INCOMING("in"), OUTGOING("out");
	
	private String txType;

	private TransactionType(String txType) {
		this.txType = txType;
	}

	public String getTransactionType() {
		return txType;
	}
	
}
