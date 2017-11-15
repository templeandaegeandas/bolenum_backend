/**
 * 
 */
package com.bolenum.enums;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
public enum CurrencyName {
	BITCOIN("bitcoin"), ETHEREUM("ethereum"), ERC20TOKEN("erc20token"),FIAT("fiat");
	private String currencyName;

	private CurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getCurrencyName() {
		return currencyName;
	}
}
