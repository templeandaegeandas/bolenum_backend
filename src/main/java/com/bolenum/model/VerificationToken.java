package com.bolenum.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.bolenum.constant.TokenType;

@Entity // This tells Hibernate to make a table out of this class
public class VerificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long tokenId;

	@ManyToOne
	@Cascade({ CascadeType.SAVE_UPDATE })
	private User user;

	private TokenType tokentype;

	private String token;

	private Timestamp createdOn;

	private Timestamp updatedOn;

	private Timestamp deletedOn;

	public VerificationToken() {
	}

	public VerificationToken(String token) {
		this.token = token;
	}

	public VerificationToken(String token, User user) {
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

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Timestamp getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Timestamp deletedOn) {
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

}
