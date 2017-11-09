package com.bolenum.enums;

public enum TwoFactorAuthOption {
	MOBILE("mobile"),GOOGLE_AUTHENTICATOR("google_authenticator"),NONE("none");
	
	private String twoFactType;

	private TwoFactorAuthOption(String twoFactType) {
		this.twoFactType = twoFactType;
	}

	public String getTransactionType() {
		return twoFactType;
	}
}