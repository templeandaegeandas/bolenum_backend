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
	private Boolean isDeleted = false;
	@OneToOne
	private AuthenticationToken authenticationToken;

	public UserActivity(String ipAddress, String browserName, AuthenticationToken authenticationToken) {
		this.ipAddress = ipAddress;
		this.browserName = browserName;
		this.authenticationToken = authenticationToken;
	}

	public UserActivity() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getBrowserName() {
		return browserName;
	}

	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public AuthenticationToken getAuthenticationToken() {
		return authenticationToken;
	}

	public void setAuthenticationToken(AuthenticationToken authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

}
