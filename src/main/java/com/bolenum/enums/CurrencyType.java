package com.bolenum.enums;

public enum CurrencyType {

	CRYPTO("crypto"), ERC20TOKEN("erc20token"), FIAT("fiat");
	private String currType;

	private CurrencyType(String currencyType) {
			this.currType = currencyType;
		}

	/**
	 * This method is use to get currency type.
	 * @param Nothing
	 * @return currType
	 */
	public String getCurrencyType() {
		return currType;
	}
}
