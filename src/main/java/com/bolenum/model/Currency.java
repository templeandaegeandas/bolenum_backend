package com.bolenum.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

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
	private Date updatedOn;
	
	@Enumerated(EnumType.STRING)
	private CurrencyType currencyType;

	@ApiModelProperty(hidden = true)
	private Date deletedOn;

	private boolean isDeleted = false;
	
	@ManyToMany
	private List<Currency> market;
	
	private Double priceBLN;
	
	private Double priceBTC;
	
	private Double priceETH;
	
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
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
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
	 * @return the market
	 */
	public List<Currency> getMarket() {
		return market;
	}

	/**
	 * @param market the market to set
	 */
	public void setMarket(List<Currency> market) {
		this.market = market;
	}

	/**
	 * @return the priceBLN
	 */
	public Double getPriceBLN() {
		return priceBLN;
	}

	/**
	 * @param priceBLN the priceBLN to set
	 */
	public void setPriceBLN(Double priceBLN) {
		this.priceBLN = priceBLN;
	}

	/**
	 * @return the priceBTC
	 */
	public Double getPriceBTC() {
		return priceBTC;
	}

	/**
	 * @param priceBTC the priceBTC to set
	 */
	public void setPriceBTC(Double priceBTC) {
		this.priceBTC = priceBTC;
	}

	/**
	 * @return the priceETH
	 */
	public Double getPriceETH() {
		return priceETH;
	}

	/**
	 * @param priceETH the priceETH to set
	 */
	public void setPriceETH(Double priceETH) {
		this.priceETH = priceETH;
	}
}
