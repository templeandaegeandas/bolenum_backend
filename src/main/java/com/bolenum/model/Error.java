package com.bolenum.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

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
	@CreationTimestamp
	private Date createdOn;
	private Date retriedOn;
	private int count;

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

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn 
	 * the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return to get the retried date and time
	 */
	public Date getRetriedOn() {
		return retriedOn;
	}

	/**
	 * @param retriedOn 
	 * the retriedOn to set
	 */
	public void setRetriedOn(Date retriedOn) {
		this.retriedOn = retriedOn;
	}

	/**
	 * @return the get the number of retry count 
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count 
	 * to set the retry count
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
}
