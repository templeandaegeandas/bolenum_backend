package com.bolenum.constant;

/**
 * @Author chandan Kumar Singh
 *
 * @Date 04-Sep-2017
 */
public class UrlConstant {
	private UrlConstant() {
	}

	public static final String BASE_URI_V1 = "api/v1/";
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
	public static final String CHANGE_PASSWORD = "change/password";
	public static final String USER_LOGOUT = BASE_URI_V1 + "logout";
	public static final String FORGET_PASS = BASE_URI_V1 + "forgetpassword";
	public static final String FORGET_PASS_VERIFY = FORGET_PASS + "/verify";
	public static final String UPDATE_USER_PROFILE = "update";
	

	public static final String LIST_USERS = "list/users";
	public static final String GET_USER_BY_ID = "user/{userId}";
	
	/********************** KYC API ***********************/
	public static final String UPLOAD_DOCUMENT = "/kyc/upload";
	public static final String APPROVE_DOCUMENT = "/kyc/approve/{userId}";
	public static final String DISAPPROVE_DOCUMENT = "/kyc/disapprove";
	public static final String SUBMITTED_KYC_LIST = "list/kyc";

}
