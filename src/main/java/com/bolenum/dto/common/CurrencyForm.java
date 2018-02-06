package com.bolenum.dto.common;

import java.util.Date;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.bolenum.enums.CurrencyType;
import com.bolenum.model.Currency;

import io.swagger.annotations.ApiModelProperty;

public class CurrencyForm {

	@ApiModelProperty(hidden = true)
	private Long currencyId;

	@NotBlank
	@Length(min = 3, message = "currency name length must be 3 characters and above")
	@Pattern(regexp = "([A-Za-z]+)", message = "currency name must be valid")
	private String currencyName;

	@NotBlank
	@Length(min = 3, message = "currency abbreviation length must be 3 characters and above")
	private String currencyAbbreviation;
	
	private CurrencyType currencyType;

	private Boolean isDeleted;

	private Date onCreated;

	private Date onUpdated;
	
	public CurrencyForm(String currencyName, String currencyAbbreviation, CurrencyType currencyType) {
		this.currencyName = currencyName;
		this.currencyAbbreviation = currencyAbbreviation;
		this.currencyType = currencyType;
	}

	

	/**
	 * @return the currencyId
	 */
	public Long getCurrencyId() {
		return currencyId;
	}



	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}



	/**
	 * @return the currencyName
	 */
	public String getCurrencyName() {
		return currencyName;
	}



	/**
	 * @param currencyName the currencyName to set
	 */
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}



	/**
	 * @return the currencyAbbreviation
	 */
	public String getCurrencyAbbreviation() {
		return currencyAbbreviation;
	}



	/**
	 * @param currencyAbbreviation the currencyAbbreviation to set
	 */
	public void setCurrencyAbbreviation(String currencyAbbreviation) {
		this.currencyAbbreviation = currencyAbbreviation;
	}



	/**
	 * @return the currencyType
	 */
	public CurrencyType getCurrencyType() {
		return currencyType;
	}



	/**
	 * @param currencyType the currencyType to set
	 */
	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
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



	public Currency copy(Currency currency) {
		currency.setCurrencyName(this.currencyName);
		currency.setCurrencyAbbreviation(this.currencyAbbreviation);
		currency.setCurrencyType(this.currencyType);
		return currency;
	}

}
