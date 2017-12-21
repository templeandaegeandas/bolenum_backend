package com.bolenum.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.NotBlank;

import com.bolenum.enums.TokenType;

/**
 * 
 * @Author Himanshu
 * @Date 12-Sep-2017
 * 
 */
@Entity
public class AuthenticationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long tokenId;

	@ManyToOne
	private User user;

	@Enumerated(EnumType.STRING)
	private TokenType tokentype;
	
	@NotBlank
	private String token;
	
	@CreationTimestamp
	private Date createdOn;
	
	@UpdateTimestamp
	private Date updatedOn;
	
	private Date deletedOn;

	private boolean isDeleted = false;
	
	public AuthenticationToken() {
	}

	public AuthenticationToken(String token) {
		this.token = token;
	}

	public AuthenticationToken(String token, User user) {
		this.token = token;
		this.user = user;
	}

	public Long getTokenId() {
		return tokenId;
	}

	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TokenType getTokentype() {
		return tokentype;
	}

	public void setTokentype(TokenType tokentype) {
		this.tokentype = tokentype;
	}

	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted 
	 * the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
