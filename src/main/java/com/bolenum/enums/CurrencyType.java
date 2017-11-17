package com.bolenum.enums;

public enum CurrencyType {

	CRYPTO("crypto"), ERC20TOKEN("erc20token"), FIAT("fiat");
	private String currencyType;

	private CurrencyType(String currencyType) {
			this.currencyType = currencyType;
		}

	public String getCurrencyType() {
		return currencyType;
	}
}
