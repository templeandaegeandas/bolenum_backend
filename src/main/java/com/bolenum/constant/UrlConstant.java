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
	public static final String CURRENCY_LIST_FOR_MARKET="currency/list/market";
	public static final String CURRENCY_PAIR="currency/pair";
	public static final String CURRENCY_PAIR_LIST="currency-pair/list";
	public static final String TRANSACTION_FEES="transaction/fees";
	public static final String PAIRED_CURRENCY="paired/currency/list";
	
	/********************** KYC API ***********************/
	public static final String UPLOAD_DOCUMENT = BASE_URI_V1 + "kyc/upload";
	public static final String APPROVE_DOCUMENT = BASE_URI_V1 + "kyc/approve";
	public static final String DISAPPROVE_DOCUMENT = BASE_URI_V1 + "kyc/disapprove";
	public static final String GET_KYC_BY_ID = BASE_URI_V1 + "kyc/{kycId}";
	public static final String SUBMITTED_KYC_LIST = BASE_URI_V1 + "list/kyc";
	public static final String GET_KYC_BY_USER_ID = BASE_URI_V1 + "list/user/kyc";
	public static final String SUBMITTED_KYC_LIST_OF_USER=BASE_URI_V1+"user/kyc/list";
	public static final String SUBMITTED_KYC_BY_USER_ID=BASE_URI_V1+"kyc/by/userid";
	
	
	/******************** Two Factor Authentication API ******/
	public static final String GEN_GOOGLE_AUTH_QR = "twofactor/auth/google/authenticator";
	public static final String VERIFY_GOOGLE_AUTH_KEY = "twofactor/auth/google/authenticator/verify";
	public static final String VERIFY_GOOGLE_AUTH_KEY_OPEN = "twofactor/auth/open";
	public static final String TWO_FACTOR_AUTH_VIA_MOBILE = "twofactor/auth/mobile";
	public static final String SEND_2FA_OTP = "twofactor/auth/send/otp";
	public static final String VERIFY_2FA_OTP = "twofactor/auth/mobile/verify";
	public static final String REMOVE_TWO_FACTOR_AUTH = "twofactor/auth/remove";
	
	/******************** USER Wallet API ******/
	public static final String DEPOSIT = "deposit";
	public static final String MARKET_PRICE = "market/price";
	public static final String WITHDRAW = "withdraw";
	public static final String GET_TRANSACTION_LIST_OF_USER_WITHDRAW="withdraw/list";
	public static final String GET_TRANSACTION_LIST_OF_USER_DEPOSIT = "deposit/list";
	public static final String DEPOSIT_TRANSACTION_STATUS="transaction/status/deposit";
	
	/******************** ORDER API ******/
	public static final String CREATE_ORDER = "create/order";
	public static final String BUY_ORDER_LIST = "get/buy/orders";
	public static final String SELL_ORDER_LIST = "get/sell/orders";
	public static final String MY_ORDER_LIST = "get/my/orders";
	public static final String TRADE_LIST_LOGGEDIN = "get/loggedin/trade/list";
	public static final String TRADE_LIST_ALL = "get/trade/list";
	
	/******************** Erc20 API ******/
	public static final String ADD_NEW_TOKEN = "add/new/token";
	public static final String GET_TOKEN_LIST = "get/token/list";
	public static final String GET_TOKEN_BY_ID = "get/token";
	
	/****************** WebSocket *****************************/
	public static final String WEBSOCKET_PATH = "/websocket";
	public static final String WS_REGISTER_ENDPOINT = "/websocket/bolenum/exchange";
	public static final String WS_APPLICATION_DEST_PREFIX = "/websocket/app";
	public static final String WS_BROKER = "/websocket/broker";
	public static final String WS_SENDER_USER = "/sender/user";
	public static final String WS_SENDER_ADMIN = "/sender/admin";
	public static final String WS_LISTNER_USER = "/listner/user";
	public static final String WS_LISTNER_ADMIN = "/listner/admin";
	public static final String WS_LISTNER_ORDER = "/listner/order";
}
	

