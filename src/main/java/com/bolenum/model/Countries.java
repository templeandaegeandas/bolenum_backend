package com.bolenum.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Countries {

	@Id
	private Long countryId;
	private String name;
	private String isoCode2;
	private String isoCode3;
	private String addressFormat;
	private String postCodeRequired;
	private Integer status;
	private String phoneCode;
	/**
	 * @return the countryId
	 */
	public Long getCountryId() {
		return countryId;
	}
	/**
	 * @param countryId the countryId to set
	 */
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the isoCode2
	 */
	public String getIsoCode2() {
		return isoCode2;
	}
	/**
	 * @param isoCode2 the isoCode2 to set
	 */
	public void setIsoCode2(String isoCode2) {
		this.isoCode2 = isoCode2;
	}
	/**
	 * @return the isoCode3
	 */
	public String getIsoCode3() {
		return isoCode3;
	}
	/**
	 * @param isoCode3 the isoCode3 to set
	 */
	public void setIsoCode3(String isoCode3) {
		this.isoCode3 = isoCode3;
	}
	/**
	 * @return the addressFormat
	 */
	public String getAddressFormat() {
		return addressFormat;
	}
	/**
	 * @param addressFormat the addressFormat to set
	 */
	public void setAddressFormat(String addressFormat) {
		this.addressFormat = addressFormat;
	}
	/**
	 * @return the postCodeRequired
	 */
	public String getPostCodeRequired() {
		return postCodeRequired;
	}
	/**
	 * @param postCodeRequired the postCodeRequired to set
	 */
	public void setPostCodeRequired(String postCodeRequired) {
		this.postCodeRequired = postCodeRequired;
	}
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * @return the phoneCode
	 */
	public String getPhoneCode() {
		return phoneCode;
	}
	/**
	 * @param phoneCode the phoneCode to set
	 */
	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}

	
}
