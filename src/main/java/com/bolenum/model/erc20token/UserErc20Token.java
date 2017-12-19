package com.bolenum.model.erc20token;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.bolenum.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class UserErc20Token {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String walletAddress;

	private Double balance;

	private String tokenName;

	private String walletJsonFile;

	private String walletPwd;

	private String walletPwdKey;

	@CreationTimestamp
	private Date createdOn;

	private Date deletedOn;

	private Boolean isDeleted = false;

	@ManyToOne
	@JsonBackReference
	private User user;

	public UserErc20Token() {

	}

	/**
	 * @param id
	 * @param walletAddress
	 * @param balance
	 * @param tokenName
	 * @param walletJsonFile
	 * @param walletPassword
	 * @param createdOn
	 * @param deletedOn
	 * @param isDeleted
	 * @param user
	 */
	public UserErc20Token(String walletAddress, Double balance, String tokenName, String walletJsonFile,
			String walletPwd, String walletPwdKey, User user) {
		super();
		this.walletAddress = walletAddress;
		this.balance = balance;
		this.tokenName = tokenName;
		this.walletJsonFile = walletJsonFile;
		this.walletPwd = walletPwd;
		this.walletPwdKey = walletPwdKey;
		this.user = user;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
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
	 * @param walletAddress
	 *            the walletAddress to set
	 */
	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	/**
	 * @return the balance
	 */
	public Double getBalance() {
		return balance;
	}

	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(Double balance) {
		this.balance = balance;
	}

	/**
	 * @return the tokenName
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * @param tokenName
	 *            the tokenName to set
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	/**
	 * @return the walletJsonFile
	 */
	public String getWalletJsonFile() {
		return walletJsonFile;
	}

	/**
	 * @param walletJsonFile
	 *            the walletJsonFile to set
	 */
	public void setWalletJsonFile(String walletJsonFile) {
		this.walletJsonFile = walletJsonFile;
	}

	/**
	 * @return the walletPwd
	 */
	public String getWalletPwd() {
		return walletPwd;
	}

	/**
	 * @param walletPwd
	 *            the walletPwd to set
	 */
	public void setWalletPwd(String walletPwd) {
		this.walletPwd = walletPwd;
	}

	/**
	 * @return the walletPwdKey
	 */
	public String getWalletPwdKey() {
		return walletPwdKey;
	}

	/**
	 * @param walletPwdKey
	 *            the walletPwdKey to set
	 */
	public void setWalletPwdKey(String walletPwdKey) {
		this.walletPwdKey = walletPwdKey;
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
	 * @return the deletedOn
	 */
	public Date getDeletedOn() {
		return deletedOn;
	}

	/**
	 * @param deletedOn
	 *            the deletedOn to set
	 */
	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

}
