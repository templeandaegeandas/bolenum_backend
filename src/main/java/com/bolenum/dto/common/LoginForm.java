/**
 * 
 */
package com.bolenum.dto.common;

import javax.validation.constraints.AssertTrue;

import org.hibernate.validator.constraints.NotBlank;

import com.bolenum.model.Role;
import com.bolenum.util.GenericUtils;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public class LoginForm {

	@NotBlank(message = "Please enter email id")
	private String emailId;

	@NotBlank(message = "Please enter password")
	private String password;
	
	@NotBlank
	private String role;

	//@NotBlank(message = "Your ip address is not present")
	private String ipAddress;
	
	//@NotBlank(message = "browser name is invalid")
	private String browserName;
	
	private String clientOsName;

	public LoginForm() {
	}

	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}

	/**
	 * @param emailId
	 *            the emailId to set
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
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * @return Role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * 
	 * @param role
	 * 		the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the browserName
	 */
	public String getBrowserName() {
		return browserName;
	}

	/**
	 * @param browserName
	 *            the browserName to set
	 */
	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	/**
	 * 
	 * @return operating system
	 */
	public String getClientOsName() {
		return clientOsName;
	}

	
	/**
	 * 
	 * @param clientOSName
	 * 		Operating system to set
	 */
	public void setClientOsName(String clientOsName) {
		this.clientOsName = clientOsName;
	}

	@AssertTrue(message = "Please enter valid email")
	private boolean isMailValid() {
		return GenericUtils.isValidMail(this.emailId);
	}

}
