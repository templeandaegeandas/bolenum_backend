package com.bolenum.enums;

public enum CurrencyType {

	CRYPTO("crypto"), ERC20TOKEN("erc20token"), FIAT("fiat");
	private String currType;

	private CurrencyType(String currencyType) {
			this.currType = currencyType;
		}

	public String getCurrencyType() {
		return currType;
	}
}
