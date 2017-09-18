/**
 * 
 */
package com.bolenum.dto.common;

import java.util.Date;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.bolenum.model.User;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chandan kumar singh
 * @date 12-Sep-2017
 */
public class UserSignupForm {
	
	@ApiModelProperty(hidden = true)
	private Long userId;

	@NotEmpty
	@Pattern(regexp = "([a-zA-Z]+)", message = "first Name must be valid")
	private String firstName;

	@Pattern(regexp = "([a-zA-Z]+)", message = "Name must be valid")
	private String middleName;

	@Pattern(regexp = "([a-zA-Z]+)", message = "Name must be valid")
	private String lastName;

	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "please enter valid email")
	@NotNull
	private String emailId;

	@NotNull
	@Length(min = 8, max = 64, message = "password length must be between 8 and 64 character")
	@Pattern.List({ @Pattern(regexp = "(?=.*[0-9]).+", message = "Password must contain one digit."),
			@Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one lowercase letter."),
			@Pattern(regexp = "(?=.*[A-Z]).+", message = "Password must contain one upper letter."),
			@Pattern(regexp = "(?=.*[!@#$%^&*+=?-_()/\"\\.,<>~`;:]).+", message = "Password must contain one special character."),
			@Pattern(regexp = "(?=\\S+$).+", message = "Password must contain no whitespace.") })
	private String password;

	@NotNull
	public String repassword = "";

	private String address;

	private String city;

	private String state;

	private String country;

	private String mobileNumber;

	private String gender;

	private Date dob;
	
	private Boolean termsConditions = false;

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName
	 *            the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
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
	 * @param city
	 *            the city to set
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
	 * @param state
	 *            the state to set
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
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber
	 *            the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
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
	 * @param dob
	 *            the dob to set
	 */
	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Boolean getTermsConditions() {
		return termsConditions;
	}

	public void setTermsConditions(Boolean termsConditions) {
		this.termsConditions = termsConditions;
	}

	@AssertTrue(message = "password did not match")
	private boolean isValid() {
		boolean check = this.password.equals(this.repassword);
		return check;
	}

	public User copy(User user) {
		user.setFirstName(this.firstName);
		user.setMiddleName(this.middleName);
		user.setLastName(this.lastName);
		user.setEmailId(this.emailId);
		user.setPassword(this.password);
		user.setAddress(this.address);
		user.setCity(this.city);
		user.setState(this.state);
		user.setCountry(this.country);
		user.setMobileNumber(this.mobileNumber);
		user.setGender(this.gender);
		user.setDob(this.dob);
		user.setTermsConditions(this.termsConditions);
		return user;
	}
}
