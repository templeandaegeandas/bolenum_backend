package com.bolenum.enums;

public enum TwoFactorAuthOption {
	MOBILE("mobile"),GOOGLE_AUTHENTICATOR("google_authenticator"),NONE("none");
	
	private String twoFactType;

	private TwoFactorAuthOption(String twoFactType) {
		this.twoFactType = twoFactType;
	}

	/**
	 * This method is use to get transaction type.
	 * @param Nothing
	 * @return twoFactType
	 */
	public String getTransactionType() {
		return twoFactType;
	}
}