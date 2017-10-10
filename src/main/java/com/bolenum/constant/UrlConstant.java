package com.bolenum.constant;

/**
 * @Author chandan Kumar Singh
 *
 * @Date 04-Sep-2017
 */
public class UrlConstant {
	private UrlConstant() {
	}

	public static final String BASE_URI_V1 = "/api/v1/";
	public static final String BASE_USER_URI_V1 = BASE_URI_V1 + "user/";
	public static final String BASE_ADMIN_URI_V1 = BASE_URI_V1 + "admin/";
	public static final String BASE_ROLE_URI_V1 = BASE_URI_V1 + "authorize/";
	public static final String BASE_PRIVILEGE_URI_V1 = BASE_URI_V1 + "action/";
	public static final String PRIVILEGE_URI = "privilege";
	public static final String ROLE_URI = "role";
	public static final String REGISTER_USER = "register";
	public static final String USER_MAIL_VERIFY = "verify";
	public static final String USER_LOGIN = BASE_URI_V1 + "login";
	public static final String USER_LOOUT = BASE_URI_V1 + "logout";
	public static final String CHANGE_PASS = "change/pass";
	public static final String USER_LOGOUT = BASE_URI_V1 + "logout";
	public static final String FORGET_PASS = BASE_URI_V1 + "forgetpassword";
	public static final String FORGET_PASS_VERIFY = FORGET_PASS + "/verify";
	public static final String UPDATE_USER_PROFILE = "update";
	public static final String GET_LOGGEDIN_USER = "get/loggedin";
	public static final String EDIT_USER_BANK_DETAILS = "bankdetails";
	public static final String ADD_USER_BANK_DETAILS = "bankdetails";
	public static final String VIEW_USER_BANK_DETAILS = "bankdetails";
	public static final String UPLOAD_PROFILE_IMAGE = "upload/image";
	public static final String ADD_MOBILE_NUMBER = "add/mobile/number";
	public static final String VERIFY_OTP = "verify/otp";
	public static final String RESEND_OTP = "resend/otp";
	public static final String GET_COUNTRIES_LIST = "countries/list";
	public static final String GET_STATE_BY_COUNTRY_ID = "states";
	
	/********************** ADMIN API ***********************/
	public static final String LIST_USERS = "list/users";
	public static final String GET_USER_BY_ID = "user/{userId}";
	public static final String VIEW_USER_BANK_DETAILS_BY_ADMIN = "admin/bankdetails";
	public static final String CURRENCY_FOR_TRADING	 = "currency";
	public static final String CURRENCY_LIST_FOR_TRADING="currency/list";
	public static final String CURRENCY_PAIR="currency/pair";
	public static final String CURRENCY_PAIR_LIST="currency-pair/list";
	
	/********************** KYC API ***********************/
	public static final String UPLOAD_DOCUMENT = BASE_URI_V1 + "/kyc/upload";
	public static final String APPROVE_DOCUMENT = BASE_URI_V1 + "/kyc/approve/{userId}";
	public static final String DISAPPROVE_DOCUMENT = BASE_URI_V1 + "/kyc/disapprove";
	public static final String GET_KYC_BY_ID = BASE_URI_V1 + "/kyc/{kycId}";
	public static final String SUBMITTED_KYC_LIST = BASE_URI_V1 + "list/kyc";
	
	/******************** Two Factor Authentication API ******/
	public static final String GEN_GOOGLE_AUTH_QR = "/twofactor/auth/google/authenticator";
	public static final String VERIFY_GOOGLE_AUTH_KEY = "/twofactor/auth/google/authenticator/verify";
	public static final String VERIFY_GOOGLE_AUTH_KEY_OPEN = "/twofactor/auth/open";
	public static final String TWO_FACTOR_AUTH_VIA_MOBILE = "/twofactor/auth/mobile";
	public static final String SEND_2FA_OTP = "/twofactor/auth/send/otp";
	public static final String VERIFY_2FA_OTP = "/twofactor/auth/mobile/verify";
	public static final String REMOVE_TWO_FACTOR_AUTH = "/twofactor/auth/remove";
	
	/******************** USER Wallet API ******/
	public static final String DEPOSIT = "deposit";
	
}
