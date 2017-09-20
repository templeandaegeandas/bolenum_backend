/**
 * 
 */
package com.bolenum.dto.common;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.bolenum.util.GenericUtils;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public class LoginForm {

	@NotBlank
	private String emailId;

	@NotNull
	private String password;

	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}

	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	@AssertTrue(message = "enter valid email")
	private boolean isMailValid() {
		boolean check = GenericUtils.isValidMail(this.emailId);
		return check;
	}
	
}
