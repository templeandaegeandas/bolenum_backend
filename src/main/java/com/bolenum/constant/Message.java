package com.bolenum.constant;

/**
 * @Author himanshu
 * @Date 11-Sep-2017
 */
public class Message {
	private Message() {

	}

	public static final String SUCCESS = "success";
	public static final String ERROR = "something went wrong";
	public static final String INVALID_REQ = "Invalid request data!!!";

	public static final String MAIL_SENT_SUCCESSFULLY = "mail sent successfully";
	public static final String INVALID_CRED = "Invalid credentials!!!";

	public static final String PRIVILEGE_ADDED_SUCCESSFULLY = "previlege added successfully";
	public static final String PRIVILEGE_REMOVED_SUCCESSFULLY = "previlege removed successfully";
	public static final String PRIVILEGE_NOT_FOUND = "privilege not found";
	public static final String PRIVILEGE_FOUND = "requested privilege found";

	public static final String ROLE_ADDED_SUCCESSFULLY = "role added successfully";
	public static final String ROLE_DELETED_SUCCESSFULLY = "role deleted successfully";
	public static final String ROLE_NOT_FOUND = "role not found";
	public static final String ROLE_FOUND = "requested role found";

	public static final String INVALID_EMAIL = "invalid email";
	public static final String EMAIL_ALREADY_EXISTS = "email already exist";

	public static final String LOGIN_SUCCESS = "login successfully";
	public static final String LOGOUT_SUCCESS = "logout.success";
	public static final String LOGOUT_FAILURE = "logout.failure";
	public static final String INVALID_TOKEN = "requested token is not valid";

	// ************USER MESSAGE***************//

	public static final String USER_NOT_FOUND = "User does not exists";
	public static final String REGIS_SUCCESS = "Check your email to complete registration";
	public static final String PASSWORD_CHANGED = "user.password.change.success";
	public static final String PASSWORD_CHANGED_FAILURE = "user.password.change.failure";
	public static final String PASSWORD_NOT_MATCHED = "New password and Confirm password not matched!";

	public static final String MAIL_VERIFY_ERROR = "Please verify your email";

	// ************ADMIN MESSAGE***************//
	public static final String USERS_LIST = "admin.user.list";
}
