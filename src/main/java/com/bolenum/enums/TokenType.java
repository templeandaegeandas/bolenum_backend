package com.bolenum.enums;

public enum TokenType {
	REGISTRATION("registration"), AUTHENTICATION("authentication"), FORGOT_PASSWORD("forget_password"), EMAIL_UPDATE(
			"email_update");

	private String type;

	private TokenType(String tokenType) {
		this.type = tokenType;
	}

	/**
	 * This method is use to get token type.
	 * @param Nothing
	 * @return type
	 */
	public String getTokenType() {
		return type;
	}
}
