/**
 * 
 */
package com.bolenum.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bolenum.enums.TransactionStatus;
import com.bolenum.enums.TransactionType;
import com.bolenum.enums.TransferStatus;

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
	@Column(unique = true)
	private String txHash;
	private String fromAddress;
	private String toAddress;
	private Double txFee;
	private Double txAmount;
	private String txDescription;

	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@Enumerated(EnumType.STRING)
	private TransactionStatus transactionStatus;
	private Double gas;
	private Double gasPrice;

	private String currencyName;

	@ManyToOne
	private User fromUser;

	@ManyToOne
	private User toUser;
	/**
	 * platform fee for withdrawal and deposit, if transactionStatus is
	 * "TRANSFER", then admin has paid fee to user's ethereum transaction
	 */
	private Double fee;

	/**
	 * transaction current status with respect to number of confirmation in
	 * blockchain
	 */
	private String txStatus;

	private Integer noOfConfirmations;
	/**
	 * for trade Id, to get the details of trade
	 */
	private Long tradeId;

	private boolean inAppTransaction = false;

	/**
	 * for checking transfer status(amount transfered to admin or not)
	 */
	@Enumerated(EnumType.STRING)
	private TransferStatus transferStatus;
	/**
	 * for which currency admin has paid transfer fee
	 */
	private String transferFeeCurrency;

	/**
	 * for checking transfer hash , whether hash can be fetch or not.
	 */
	private boolean isFetchStatus=false;

	/**
	 * @return the isFetchStatus
	 */
	public boolean isFetchStatus() {
		return isFetchStatus;
	}

	/**
	 * @param isFetchStatus the isFetchStatus to set
	 */
	public void setFetchStatus(boolean isFetchStatus) {
		this.isFetchStatus = isFetchStatus;
	}

	public Transaction() {

	}

	public Transaction(String txHash, String fromAddress, String toAddress, Double txFee, Double txAmmount,
			String txDescription, TransactionType transactionType, Double gas, Double gasPrice, User fromUser,
			User toUser) {

		this.txHash = txHash;
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.txFee = txFee;
		this.txAmount = txAmmount;
		this.txDescription = txDescription;
		this.transactionType = transactionType;
		this.gas = gas;
		this.gasPrice = gasPrice;
		this.fromUser = fromUser;
		this.toUser = toUser;
		
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

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
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
	public Double getTxAmount() {
		return txAmount;
	}

	/**
	 * @param txAmmount
	 *            the txAmmount to set
	 */
	public void setTxAmount(Double txAmount) {
		this.txAmount = txAmount;
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
	 * 
	 * @return the transactionType
	 */
	public TransactionType getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType
	 *            the transactionType to set
	 */
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the gas which is ethereum tx fee
	 */
	public Double getGas() {
		return gas;
	}

	/**
	 * @param gas
	 *            the gas to set
	 */
	public void setGas(Double gas) {
		this.gas = gas;
	}

	/**
	 * @return the gasPrice
	 */
	public Double getGasPrice() {
		return gasPrice;
	}

	/**
	 * @param gasPrice
	 *            the gasPrice to set
	 */
	public void setGasPrice(Double gasPrice) {
		this.gasPrice = gasPrice;
	}

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public User getToUser() {
		return toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	/**
	 * @return the txStatus
	 */
	public String getTxStatus() {
		return txStatus;
	}

	/**
	 * @param txStatus
	 *            the txStatus to set
	 */
	public void setTxStatus(String txStatus) {
		this.txStatus = txStatus;
	}

	/**
	 * @return the noOfConfirmations
	 */
	public Integer getNoOfConfirmations() {
		return noOfConfirmations;
	}

	/**
	 * @param noOfConfirmations
	 *            the noOfConfirmations to set
	 */
	public void setNoOfConfirmations(Integer noOfConfirmations) {
		this.noOfConfirmations = noOfConfirmations;
	}

	/**
	 * @return the tradeId
	 */
	public Long getTradeId() {
		return tradeId;
	}

	/**
	 * @param tradeId
	 *            the tradeId to set
	 */
	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}

	/**
	 * @return the inAppTransaction
	 */
	public boolean isInAppTransaction() {
		return inAppTransaction;
	}

	/**
	 * @param inAppTransaction
	 *            the inAppTransaction to set
	 */
	public void setInAppTransaction(boolean inAppTransaction) {
		this.inAppTransaction = inAppTransaction;
	}

	/**
	 * @return the transferStatus
	 */
	public TransferStatus getTransferStatus() {
		return transferStatus;
	}

	/**
	 * @param transferStatus
	 *            the transferStatus to set
	 */
	public void setTransferStatus(TransferStatus transferStatus) {
		this.transferStatus = transferStatus;
	}

	/**
	 * @return the transferFeeCurrency
	 */
	public String getTransferFeeCurrency() {
		return transferFeeCurrency;
	}

	/**
	 * @param transferFeeCurrency
	 *            the transferFeeCurrency to set
	 */
	public void setTransferFeeCurrency(String transferFeeCurrency) {
		this.transferFeeCurrency = transferFeeCurrency;
	}

}
