package com.bolenum.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bolenum.enums.TwoFactorAuthOption;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @Author Himanshu Kumar
 * @Date 13-Oct-2017
 */

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;

	@NotNull
	private String firstName;

	private String middleName;

	private String lastName;

	@NotNull
	@Column(unique = true)
	private String emailId;

	@NotNull
	private String password;

	private String address;

	private String city;

	private String state;

	private String country;

	private String countryCode;

	private String mobileNumber;

	private Boolean isMobileVerified = false;

	private String gender;

	private Date dob;

	private String profileImage;

	private Boolean termsConditions = false;

	@ApiModelProperty(hidden = true)
	private Boolean isEnabled = false;

	@ApiModelProperty(hidden = true)
	private Boolean isDeleted = false;

	@ApiModelProperty(hidden = true)
	private Boolean isLocked = false;

	@ApiModelProperty(hidden = true)
	@CreationTimestamp
	private Date createdOn;

	@ApiModelProperty(hidden = true)
	@UpdateTimestamp
	private Date updatedOn;

	@ApiModelProperty(hidden = true)
	private Date deletedOn;

	@OneToOne
	private Role role;

	private String btcWalletUuid;

	private String ethWalletaddress;

	private String ethWalletPwd;

	private String ethWalletPwdKey;

	private String ethWalletJsonFileName;

	@Enumerated(EnumType.STRING)
	private TwoFactorAuthOption twoFactorAuthOption = TwoFactorAuthOption.NONE;

	private String google2FaAuthKey;

	private String btcWalletAddress;

	public String getBtcWalletAddress() {
		return btcWalletAddress;
	}

	public void setBtcWalletAddress(String btcWalletAddress) {
		this.btcWalletAddress = btcWalletAddress;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return this.firstName + (this.middleName == null ? "" : this.middleName)
				+ (this.lastName == null ? "" : this.lastName);
	}



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
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the isMobileVerified
	 */
	public Boolean getIsMobileVerified() {
		return isMobileVerified;
	}

	/**
	 * @param isMobileVerified the isMobileVerified to set
	 */
	public void setIsMobileVerified(Boolean isMobileVerified) {
		this.isMobileVerified = isMobileVerified;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the dob
	 */
	public Date getDob() {
		return dob;
	}

	/**
	 * @param dob the dob to set
	 */
	public void setDob(Date dob) {
		this.dob = dob;
	}

	/**
	 * @return the termsConditions
	 */
	public Boolean getTermsConditions() {
		return termsConditions;
	}

	/**
	 * @param termsConditions the termsConditions to set
	 */
	public void setTermsConditions(Boolean termsConditions) {
		this.termsConditions = termsConditions;
	}

	/**
	 * @return the isEnabled
	 */
	public Boolean getIsEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled the isEnabled to set
	 */
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
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
	 * @return the isLocked
	 */
	public Boolean getIsLocked() {
		return isLocked;
	}

	/**
	 * @param isLocked the isLocked to set
	 */
	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn
	 *            the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the updatedOn
	 */
	public Date getUpdatedOn() {
		return updatedOn;
	}

	/**
	 * @param updatedOn
	 *            the updatedOn to set
	 */
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	/**
	 * @return the deletedOn
	 */
	public Date getDeletedOn() {
		return deletedOn;
	}

	/**
	 * @param deletedOn
	 *            the deletedOn to set
	 */
	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	/**
	 * @return the btcWalletUuid
	 */
	public String getBtcWalletUuid() {
		return btcWalletUuid;
	}

	/**
	 * @param btcWalletUuid
	 *            the btcWalletUuid to set
	 */
	public void setBtcWalletUuid(String btcWalletUuid) {
		this.btcWalletUuid = btcWalletUuid;
	}

	/**
	 * @return the ethWalletaddress
	 */
	public String getEthWalletaddress() {
		return ethWalletaddress;
	}

	/**
	 * @param ethWalletaddress
	 *            the ethWalletaddress to set
	 */
	public void setEthWalletaddress(String ethWalletaddress) {
		this.ethWalletaddress = ethWalletaddress;
	}

	/**
	 * @return the ethWalletPwd
	 */
	public String getEthWalletPwd() {
		return ethWalletPwd;
	}

	/**
	 * @param ethWalletPwd
	 *            the ethWalletPwd to set
	 */
	public void setEthWalletPwd(String ethWalletPwd) {
		this.ethWalletPwd = ethWalletPwd;
	}

	public TwoFactorAuthOption getTwoFactorAuthOption() {
		return twoFactorAuthOption;
	}

	public void setTwoFactorAuthOption(TwoFactorAuthOption twoFactorAuthOption) {
		this.twoFactorAuthOption = twoFactorAuthOption;
	}

	public String getGoogle2FaAuthKey() {
		return google2FaAuthKey;
	}

	public void setGoogle2FaAuthKey(String google2FaAuthKey) {
		this.google2FaAuthKey = google2FaAuthKey;
	}

	/**
	 * @return the ethWalletPwdKey
	 */
	public String getEthWalletPwdKey() {
		return ethWalletPwdKey;
	}

	/**
	 * @param ethWalletPwdKey
	 *            the ethWalletPwdKey to set
	 */
	public void setEthWalletPwdKey(String ethWalletPwdKey) {
		this.ethWalletPwdKey = ethWalletPwdKey;
	}

	/**
	 * @return the ethWalletJsonFileName
	 */
	public String getEthWalletJsonFileName() {
		return ethWalletJsonFileName;
	}

	/**
	 * @param ethWalletJsonFileName
	 *            the ethWalletJsonFileName to set
	 */
	public void setEthWalletJsonFileName(String ethWalletJsonFileName) {
		this.ethWalletJsonFileName = ethWalletJsonFileName;
	}
}
