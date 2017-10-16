package com.bolenum.enums;

public enum CurrencyType {

	CRYPTO("CRYPTO"), ERC20TOKEN("ERC20TOKEN"), FIAT("FIAT");
	private String currencyType;

	private CurrencyType(String currencyType) {
			this.currencyType = currencyType;
		}

	public String getCurrencyType() {
		return currencyType;
	}
}
