package com.bolenum.enums;

public enum TokenType {
	REGISTRATION("registration"), AUTHENTICATION("authentication"), FORGOT_PASSWORD("forget_password"), EMAIL_UPDATE(
			"email_update");

	private String tokenType;

	private TokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getTokenType() {
		return tokenType;
	}
}
