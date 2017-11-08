/**
 * 
 */
package com.bolenum.enums;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
public enum CurrencyName {
	BITCOIN("BTC"), ETHEREUM("ETH"), ERC20TOKEN("ERC20");
	private String currencyType;

	private CurrencyName(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getCurrencyType() {
		return currencyType;
	}
}
