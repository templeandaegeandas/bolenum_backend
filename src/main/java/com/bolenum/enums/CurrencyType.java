/**
 * 
 */
package com.bolenum.enums;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
public enum CurrencyType {
	BITCOIN("BTC"), ETHEREUM("ETH"), BOLENO("BLN");
	private String currencyType;

	private CurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getCurrencyType() {
		return currencyType;
	}
}
