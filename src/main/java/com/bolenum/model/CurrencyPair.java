package com.bolenum.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Author Himanshu
 * @Date 06-Oct-2017
 */

@Entity
public class CurrencyPair {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long pairId;

	@ManyToMany
	private List<Currency> toCurrency;

	@ManyToMany
	private List<Currency> pairedCurrency;

	@NotBlank
	@Column(unique = true)
	private String pairName;

	@ApiModelProperty(hidden = true)
	@CreationTimestamp
	private Date onCreated;

	@ApiModelProperty(hidden = true)
	@UpdateTimestamp
	private Date onUpdated;

	@ApiModelProperty(hidden = true)
	private Boolean isEnabled;

	@ApiModelProperty(hidden = true)
	private Boolean isDeleted;

	@ApiModelProperty(hidden = true)
	private Date deletedOn;

	public Long getPairId() {
		return pairId;
	}

	public void setPairId(Long pairId) {
		this.pairId = pairId;
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

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

}
