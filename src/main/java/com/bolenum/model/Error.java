package com.bolenum.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.GenerationType;

@Entity
public class Error {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String fromAddress;
	private String toAddress;
	private String error;
	private String currency;
	private Double amount;
	private Boolean canRetry;
	
	public Error(String fromAddress, String toAddress, String error, String currency, Double amount, Boolean canRetry) {
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.error = error;
		this.currency = currency;
		this.amount = amount;
		this.canRetry = canRetry;
	}
	
	public Error() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Boolean getCanRetry() {
		return canRetry;
	}

	public void setCanRetry(Boolean canRetry) {
		this.canRetry = canRetry;
	}
}
