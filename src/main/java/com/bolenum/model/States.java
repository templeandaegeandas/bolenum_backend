package com.bolenum.model;

import javax.persistence.Entity;
import javax.persistence.Id;
/**
 * 
 * @author Vishal Kumar
 *
 */
@Entity
public class States {

	@Id
	private Long stateId;
	private Long countryId;
	private String stateName;
	private String stateCode;
	private Integer status;

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
