/**
 * 
 */
package com.bolenum.model;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bolenum.enums.CurrencyType;
import com.bolenum.enums.TransactionType;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chandan kumar singh
 * @date 28-Sep-2017
 */

@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ApiModelProperty(hidden = true)
	@CreationTimestamp
	private Date createdOn;

	@ApiModelProperty(hidden = true)
	@UpdateTimestamp
	private Date updatedOn;
	@Column(unique=true)
	private String txHash;
	private String fromAddress;
	private String toAddress;
	private Double txFee;
	private Double txAmmount;
	private String txDescription;
	private CurrencyType currencyType;
	private TransactionType transactionType;
	private BigInteger gas;
	private BigInteger gasPrice;

	public Transaction() {

	}

	public Transaction(String txHash, String fromAddress, String toAddress, Double txFee, Double txAmmount,
			String txDescription, CurrencyType currencyType, TransactionType transactionType, BigInteger gas,BigInteger gasPrice) {
		this.txHash = txHash;
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.txFee = txFee;
		this.txAmmount = txAmmount;
		this.txDescription = txDescription;
		this.currencyType = currencyType;
		this.transactionType = transactionType;
		this.gas = gas;
		this.gasPrice = gasPrice;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * the id to set
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * the createdOn to set
	 * 
	 * @param createdOn
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the updatedOn
	 */
	public Date getUpdatedOn() {
		return updatedOn;
	}

	/**
	 * the updatedOn to set
	 * 
	 * @param updatedOn
	 */
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	/**
	 * @return the txHash
	 */
	public String getTxHash() {
		return txHash;
	}

	/**
	 * the txHash to set
	 * 
	 * @param txHash
	 */
	public void setTxHash(String txHash) {
		this.txHash = txHash;
	}

	/**
	 * @return the fromAddress
	 */
	public String getFromAddress() {
		return fromAddress;
	}

	/**
	 * the fromAddress to set
	 * 
	 * @param fromAddress
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
	 * @param toAddress
	 *            the toAddress to set
	 */
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	/**
	 * @return the txFee
	 */
	public Double getTxFee() {
		return txFee;
	}

	/**
	 * @param txFee
	 *            the txFee to set
	 */
	public void setTxFee(Double txFee) {
		this.txFee = txFee;
	}

	/**
	 * @return the txAmmount
	 */
	public Double getTxAmmount() {
		return txAmmount;
	}

	/**
	 * @param txAmmount
	 *            the txAmmount to set
	 */
	public void setTxAmmount(Double txAmmount) {
		this.txAmmount = txAmmount;
	}

	/**
	 * @return the txDescription
	 */
	public String getTxDescription() {
		return txDescription;
	}

	/**
	 * @param txDescription
	 *            the txDescription to set
	 */
	public void setTxDescription(String txDescription) {
		this.txDescription = txDescription;
	}

	/**
	 * @return the currencyType
	 */
	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	/**
	 * @param currencyType
	 *  the currencyType to set
	 */
	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
	}

	/**
	 * @return the transactionType
	 */
	public TransactionType getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType
	 * the transactionType to set
	 */
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the gas which is ethereum tx fee
	 */
	public BigInteger getGas() {
		return gas;
	}

	/**
	 * @param gas
	 *            the gas to set
	 */
	public void setGas(BigInteger gas) {
		this.gas = gas;
	}

	/**
	 * @return the gasPrice
	 */
	public BigInteger getGasPrice() {
		return gasPrice;
	}

	/**
	 * @param gasPrice 
	 * the gasPrice to set
	 */
	public void setGasPrice(BigInteger gasPrice) {
		this.gasPrice = gasPrice;
	}

}
