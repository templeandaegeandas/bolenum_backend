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

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsoCode2() {
		return isoCode2;
	}

	public void setIsoCode2(String isoCode2) {
		this.isoCode2 = isoCode2;
	}

	public String getIsoCode3() {
		return isoCode3;
	}

	public void setIsoCode3(String isoCode3) {
		this.isoCode3 = isoCode3;
	}

	public String getAddressFormat() {
		return addressFormat;
	}

	public void setAddressFormat(String addressFormat) {
		this.addressFormat = addressFormat;
	}

	public String getPostCodeRequired() {
		return postCodeRequired;
	}

	public void setPostCodeRequired(String postCodeRequired) {
		this.postCodeRequired = postCodeRequired;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPhoneCode() {
		return phoneCode;
	}

	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}
}
