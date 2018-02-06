package com.bolenum.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

/**
 * 
 * @author Himanshu Kumar
 *
 */
@Entity
public class Error {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String fromAddress;
	private String toAddress;
	private String errorMsg;
	private String currency;
	private Double amount;
	private Boolean canRetry;
	@CreationTimestamp
	private Date createdOn;
	private Date retriedOn;
	private int count;
	private Long tradeId;

	public Error(String fromAddress, String toAddress, String error, String currency, Double amount, Boolean canRetry,
			Long tradeId) {
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.errorMsg = error;
		this.currency = currency;
		this.amount = amount;
		this.canRetry = canRetry;
		this.tradeId = tradeId;
	}

	public Error() {

	}



	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the fromAddress
	 */
	public String getFromAddress() {
		return fromAddress;
	}

	/**
	 * @param fromAddress the fromAddress to set
	 */
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	/**
	 * @return the toAddress
	 */
	public String getToAddress() {
		return toAddress;
	}

	/**
	 * @param toAddress the toAddress to set
	 */
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the canRetry
	 */
	public Boolean getCanRetry() {
		return canRetry;
	}

	/**
	 * @param canRetry the canRetry to set
	 */
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
	 *            the createdOn to set
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
	 *            the retriedOn to set
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
	 *            to set the retry count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the tradeId
	 */
	public Long getTradeId() {
		return tradeId;
	}

	/**
	 * @param tradeId the tradeId to set
	 */
	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}
	

}
