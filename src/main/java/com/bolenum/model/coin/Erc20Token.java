package com.bolenum.model.coin;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.validator.constraints.NotBlank;

import com.bolenum.model.Currency;

/**
 * 
 * @author Vishal Kumar
 * @date 04-Oct-2017
 *
 */
@Entity
public class Erc20Token {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String walletAddress;
	@NotBlank
	private String contractAddress;
	@OneToOne
	private Currency currency;
	private Double decimalValue;
	private Boolean isDeleted = false;
	private Date createdDate = new Date();
	
	public Erc20Token(String walletAddress, String contractAddress, Currency currency, double decimalValue) {
		this.walletAddress = walletAddress;
		this.contractAddress = contractAddress;
		this.currency = currency;
		this.decimalValue = decimalValue;
	}
	
	public Erc20Token() {
		
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
	 * @return the walletAddress
	 */
	public String getWalletAddress() {
		return walletAddress;
	}

	/**
	 * @param walletAddress the walletAddress to set
	 */
	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	/**
	 * @return the contractAddress
	 */
	public String getContractAddress() {
		return contractAddress;
	}

	/**
	 * @param contractAddress the contractAddress to set
	 */
	public void setContractAddress(String contractAddress) {
		this.contractAddress = contractAddress;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the decimalValue
	 */
	public Double getDecimalValue() {
		return decimalValue;
	}

	/**
	 * @param decimalValue the decimalValue to set
	 */
	public void setDecimalValue(Double decimalValue) {
		this.decimalValue = decimalValue;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


}
