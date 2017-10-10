package com.bolenum.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
	private String binaryKey;
	private String walletAddress;
	private String contractAddress;
	private Boolean isDeleted = false;
	private Date createdDate = new Date();
	
	public Erc20Token(String binaryKey, String walletAddress, String contractAddress) {
		this.binaryKey = binaryKey;
		this.walletAddress = walletAddress;
		this.contractAddress = contractAddress;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBinaryKey() {
		return binaryKey;
	}

	public void setBinaryKey(String binaryKey) {
		this.binaryKey = binaryKey;
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
