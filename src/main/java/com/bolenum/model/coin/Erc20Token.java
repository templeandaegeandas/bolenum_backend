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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public String getContractAddress() {
		return contractAddress;
	}

	public void setContractAddress(String contractAddress) {
		this.contractAddress = contractAddress;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Double getDecimalValue() {
		return decimalValue;
	}

	public void setDecimalValue(Double decimalValue) {
		this.decimalValue = decimalValue;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
