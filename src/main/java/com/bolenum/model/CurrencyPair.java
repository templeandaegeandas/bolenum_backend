package com.bolenum.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author Himanshu
 * @Date 06-Oct-2017
 */

@Entity
public class CurrencyPair {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long pairId;

	@OneToMany
	private List<Currency> toCurrency;

	@OneToMany
	private List<Currency> pairedCurrency;

	@NotBlank
	@Column(unique = true)
	private String pairName;

	private Date onCreated = new Date();

	private Date onUpdated;

	private Boolean isEnabled;

	private Boolean isDeleted;

	public Long getPairID() {
		return pairId;
	}

	public void setPairID(Long pairID) {
		this.pairId = pairID;
	}

	public List<Currency> getToCurrency() {
		return toCurrency;
	}

	public void setToCurrency(List<Currency> toCurrency) {
		this.toCurrency = toCurrency;
	}

	public List<Currency> getPairedCurrency() {
		return pairedCurrency;
	}

	public void setPairedCurrency(List<Currency> pairedCurrency) {
		this.pairedCurrency = pairedCurrency;
	}

	public String getPairName() {
		return pairName;
	}

	public void setPairName(String pairName) {
		this.pairName = pairName;
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

	public Boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	

}
