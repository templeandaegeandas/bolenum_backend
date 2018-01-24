package com.bolenum.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 
 * @author vishal_kumar
 * @date 14-sep-2017
 */

@Entity
public class UserActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Date loginTime = new Date();
	private Date logoutTime;
	private String ipAddress;
	private String browserName;
	private String clientOsName;
	private Boolean isDeleted = false;
	@OneToOne
	private AuthenticationToken authenticationToken;

	public UserActivity(String ipAddress, String browserName, String clientOsName, AuthenticationToken authenticationToken) {
		this.ipAddress = ipAddress;
		this.browserName = browserName;
		this.clientOsName = clientOsName;
		this.authenticationToken = authenticationToken;
	}

	public UserActivity() {

	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the loginTime
	 */
	public Date getLoginTime() {
		return loginTime;
	}

	/**
	 * @param loginTime the loginTime to set
	 */
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	/**
	 * @return the logoutTime
	 */
	public Date getLogoutTime() {
		return logoutTime;
	}

	/**
	 * @param logoutTime the logoutTime to set
	 */
	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
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
	 * @param browserName the browserName to set
	 */
	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	/**
	 * @return the clientOsName
	 */
	public String getClientOsName() {
		return clientOsName;
	}

	/**
	 * @param clientOsName the clientOsName to set
	 */
	public void setClientOsName(String clientOsName) {
		this.clientOsName = clientOsName;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the authenticationToken
	 */
	public AuthenticationToken getAuthenticationToken() {
		return authenticationToken;
	}

	/**
	 * @param authenticationToken the authenticationToken to set
	 */
	public void setAuthenticationToken(AuthenticationToken authenticationToken) {
		this.authenticationToken = authenticationToken;
	}
}
