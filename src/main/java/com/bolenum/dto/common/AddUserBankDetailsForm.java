package com.bolenum.dto.common;

import java.util.Date;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.bolenum.model.BankAccountDetails;

import io.swagger.annotations.ApiModelProperty;

public class AddUserBankDetailsForm {

	@ApiModelProperty(hidden = true)
	private Long id;

	@NotBlank
	@Length(min = 3, message = "First name length must be 3 characters and above")
	private String accountHolderName;

	@NotBlank
	// @Pattern(regexp = "([A-Za-z0-9]+)", message = "Account number must be valid")
	private String accountNumber;

	@NotBlank
	private String bankName;

	private String address;
	private String city;
	private String district;
	private String state;
	private String branch;

	private Long contactNumber;
	private Boolean isDeleted = false;
	private Date onCreated;
	private Date onUpdated;

	

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
	 * @return the accountHolderName
	 */
	public String getAccountHolderName() {
		return accountHolderName;
	}



	/**
	 * @param accountHolderName the accountHolderName to set
	 */
	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}



	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return accountNumber;
	}



	/**
	 * @param accountNumber the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}



	/**
	 * @return the bankName
	 */
	public String getBankName() {
		return bankName;
	}



	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}



	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}



	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}



	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}



	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}



	/**
	 * @return the district
	 */
	public String getDistrict() {
		return district;
	}



	/**
	 * @param district the district to set
	 */
	public void setDistrict(String district) {
		this.district = district;
	}



	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}



	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}



	/**
	 * @return the branch
	 */
	public String getBranch() {
		return branch;
	}



	/**
	 * @param branch the branch to set
	 */
	public void setBranch(String branch) {
		this.branch = branch;
	}



	/**
	 * @return the contactNumber
	 */
	public Long getContactNumber() {
		return contactNumber;
	}



	/**
	 * @param contactNumber the contactNumber to set
	 */
	public void setContactNumber(Long contactNumber) {
		this.contactNumber = contactNumber;
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
	 * @return the onCreated
	 */
	public Date getOnCreated() {
		return onCreated;
	}



	/**
	 * @param onCreated the onCreated to set
	 */
	public void setOnCreated(Date onCreated) {
		this.onCreated = onCreated;
	}



	/**
	 * @return the onUpdated
	 */
	public Date getOnUpdated() {
		return onUpdated;
	}



	/**
	 * @param onUpdated the onUpdated to set
	 */
	public void setOnUpdated(Date onUpdated) {
		this.onUpdated = onUpdated;
	}



	public BankAccountDetails copy(BankAccountDetails bankAccountDetails) {

		bankAccountDetails.setAccountHolderName(this.accountHolderName);
		bankAccountDetails.setAccountNumber(this.accountNumber);
		bankAccountDetails.setAddress(this.address);
		bankAccountDetails.setBankName(this.bankName);
		bankAccountDetails.setBranch(this.branch);
		bankAccountDetails.setCity(this.city);
		bankAccountDetails.setContactNumber(this.contactNumber);
		bankAccountDetails.setDistrict(this.district);
		return bankAccountDetails;
	}
}
