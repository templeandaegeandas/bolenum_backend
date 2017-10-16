package com.bolenum.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @Author Himanshu
 * @Date 05-Oct-2017
 */
@Entity
public class Currency {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long currencyId;

	@NotBlank
	@Column(unique = true)
	private String currencyName;

	@NotBlank
	@Column(unique = true)
	private String currencyAbbreviation;

	@ApiModelProperty(hidden = true)
	@CreationTimestamp
	private Date createdOn = new Date();

	@ApiModelProperty(hidden = true)
	@UpdateTimestamp
	private Date UpdatedOn;

	@ApiModelProperty(hidden = true)
	private Date deletedOn;

	private boolean isDeleted = false;
	
	public Currency(String currencyName, String currencyAbbreviation) {
		this.currencyName = currencyName;
		this.currencyAbbreviation = currencyAbbreviation;
	}
	
	public Currency() {
		
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getCurrencyAbbreviation() {
		return currencyAbbreviation;
	}

	public void setCurrencyAbbreviation(String currencyAbbreviation) {
		this.currencyAbbreviation = currencyAbbreviation;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return UpdatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		UpdatedOn = updatedOn;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
