package com.bolenum.model.orders.book;

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

import com.bolenum.enums.DisputeStatus;
import com.bolenum.model.User;

/**
 * 
 * @author Himanshu Kumar
 *
 *         used to raise dispute by buyer user when buyer does not get currency
 *         against his order after pay for that
 */

@Entity
public class DisputeOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private User disputeRaiser;

	@ManyToOne
	private User disputeRaisedAgainst;

	@ManyToOne
	private Orders orders;

	private Long transactionId;

	private String firstDocumenForProofToDispute;

	private String secondDocumenForProofToDispute;

	private Date deletedOn;

	@Enumerated(EnumType.STRING)
	private DisputeStatus disputeStatus;

	@Column(length = 1337)
	private String commentByDisputeRaiser;

	@Column(length = 1337)
	private String commentByDisputeRaisedAgainst;

	@Column(length = 1337)
	private String commentForDisputeRaiser;

	@Column(length = 1337)
	private String commentForDisputeRaisedAgainst;

	@Column(length = 1337)
	private String reason;

	@CreationTimestamp
	private Date createdOn;

	@UpdateTimestamp
	private Date updatedOn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getDisputeRaiser() {
		return disputeRaiser;
	}

	public void setDisputeRaiser(User disputeRaiser) {
		this.disputeRaiser = disputeRaiser;
	}

	public User getDisputeRaisedAgainst() {
		return disputeRaisedAgainst;
	}

	public void setDisputeRaisedAgainst(User disputeRaisedAgainst) {
		this.disputeRaisedAgainst = disputeRaisedAgainst;
	}

	public Orders getOrders() {
		return orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getFirstDocumenForProofToDispute() {
		return firstDocumenForProofToDispute;
	}

	public void setFirstDocumenForProofToDispute(String firstDocumenForProofToDispute) {
		this.firstDocumenForProofToDispute = firstDocumenForProofToDispute;
	}

	public String getSecondDocumenForProofToDispute() {
		return secondDocumenForProofToDispute;
	}

	public void setSecondDocumenForProofToDispute(String secondDocumenForProofToDispute) {
		this.secondDocumenForProofToDispute = secondDocumenForProofToDispute;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	public DisputeStatus getDisputeStatus() {
		return disputeStatus;
	}

	public void setDisputeStatus(DisputeStatus disputeStatus) {
		this.disputeStatus = disputeStatus;
	}

	public String getCommentByDisputeRaiser() {
		return commentByDisputeRaiser;
	}

	public void setCommentByDisputeRaiser(String commentByDisputeRaiser) {
		this.commentByDisputeRaiser = commentByDisputeRaiser;
	}

	public String getCommentByDisputeRaisedAgainst() {
		return commentByDisputeRaisedAgainst;
	}

	public void setCommentByDisputeRaisedAgainst(String commentByDisputeRaisedAgainst) {
		this.commentByDisputeRaisedAgainst = commentByDisputeRaisedAgainst;
	}

	public String getCommentForDisputeRaiser() {
		return commentForDisputeRaiser;
	}

	public void setCommentForDisputeRaiser(String commentForDisputeRaiser) {
		this.commentForDisputeRaiser = commentForDisputeRaiser;
	}

	public String getCommentForDisputeRaisedAgainst() {
		return commentForDisputeRaisedAgainst;
	}

	public void setCommentForDisputeRaisedAgainst(String commentForDisputeRaisedAgainst) {
		this.commentForDisputeRaisedAgainst = commentForDisputeRaisedAgainst;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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
}
