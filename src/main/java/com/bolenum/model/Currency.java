package com.bolenum.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import com.bolenum.enums.CurrencyType;

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
	
	@Enumerated(EnumType.STRING)
	private CurrencyType currencyType;

	@ApiModelProperty(hidden = true)
	private Date deletedOn;

	private boolean isDeleted = false;
	
	private Double priceUSD;

	private Double priceBTC;
	
	private Double priceNGN;
	
	public Currency() {

	}

	public Currency(String currencyName, String currencyAbbreviation,CurrencyType currencyType) {
		this.currencyName = currencyName;
		this.currencyAbbreviation = currencyAbbreviation;
		this.currencyType=currencyType;
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

	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
	}
	/**
	 * @return the priceUSD
	 */
	public Double getPriceUSD() {
		return priceUSD;
	}

	/**
	 * @param priceUSD
	 *            the priceUSD to set
	 */
	public void setPriceUSD(Double priceUSD) {
		this.priceUSD = priceUSD;
	}

	/**
	 * @return the price_BTC
	 */
	public Double getPriceBTC() {
		return priceBTC;
	}

	/**
	 * @param price_BTC
	 *            the price_BTC to set
	 */
	public void setPriceBTC(Double price_BTC) {
		this.priceBTC = price_BTC;
	}

	public Double getPriceNGN() {
		return priceNGN;
	}

	public void setPriceNGN(Double priceNGN) {
		this.priceNGN = priceNGN;
	}

}
