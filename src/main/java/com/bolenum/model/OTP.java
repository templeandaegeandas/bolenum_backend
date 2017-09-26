package com.bolenum.model;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class OTP {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String mobileNumber;
	private Integer otp;
	private Boolean isDeleted = false;
	private Date createdDate = new Date();
	private User user;
	
	public OTP(String mobileNumber, Integer otp, User user) {
		this.mobileNumber = mobileNumber;
		this.otp = otp;
		this.user = user;
	}
	
	public OTP() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Integer getOtp() {
		return otp;
	}

	public void setOtp(Integer otp) {
		this.otp = otp;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
