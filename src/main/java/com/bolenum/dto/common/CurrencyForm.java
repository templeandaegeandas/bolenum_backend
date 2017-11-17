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

	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
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

	public Currency copy(Currency currency) {
		currency.setCurrencyName(this.currencyName);
		currency.setCurrencyAbbreviation(this.currencyAbbreviation);
		currency.setCurrencyType(this.currencyType);
		return currency;
	}

}
