package com.bolenum.dto.common;

import java.util.Date;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @Author Himanshu
 * @Date 24-Sep-2017
 */
public class EditUserBankDetailsForm {

	@ApiModelProperty(hidden = true)
	private Long id;

	@Length(min = 3, message = "account holder name length must be 3 characters and above")
	private String accountHolderName;

	@Pattern(regexp = "([A-Za-z0-9]+)", message = "Account number must be valid")
	private String accountNumber;
	
	private String bankName;

	@Pattern(regexp = "([A-Za-z0-9]+)", message = "IFSCCode must be valid")
	private String ifscCode;

	private String address;
	private String city;
	private String district;
	private String state;
	private String branch;

	private Long contactNumber;
	private Boolean isDeleted = false;
	private Date onCreated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public Long getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(Long contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Date getOnCreated() {
		return onCreated;
	}

	public void setOnCreated(Date onCreated) {
		this.onCreated = onCreated;
	}

	public Date getOnUpdated() {
		return onUpdated;
	}

	public void setOnUpdated(Date onUpdated) {
		this.onUpdated = onUpdated;
	}

	private Date onUpdated;

}
