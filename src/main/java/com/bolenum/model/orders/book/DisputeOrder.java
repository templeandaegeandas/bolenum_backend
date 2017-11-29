package com.bolenum.model.orders.book;

import java.util.Date;

import javax.persistence.Entity;
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
 * used to raise dispute by buyer user when buyer does not get currency 
 * against his order after pay for that 
 */

@Entity
public class DisputeOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	private User disputeRaiser;

	private Long orderId;

	private Long transactionId;

	private String firstdocumenForProofToDispute;

	private String seconddocumenForProofToDispute;

	private String comment;

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

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getFirstdocumenForProofToDispute() {
		return firstdocumenForProofToDispute;
	}

	public void setFirstdocumenForProofToDispute(String firstdocumenForProofToDispute) {
		this.firstdocumenForProofToDispute = firstdocumenForProofToDispute;
	}

	public String getSeconddocumenForProofToDispute() {
		return seconddocumenForProofToDispute;
	}

	public void setSeconddocumenForProofToDispute(String seconddocumenForProofToDispute) {
		this.seconddocumenForProofToDispute = seconddocumenForProofToDispute;
	}

	private Date deletedOn;

	private DisputeStatus disputeStatus;

	public DisputeOrder() {

	}

	public User getDisputeRaiser() {
		return disputeRaiser;
	}

	public void setDisputeRaiser(User disputeRaiser) {
		this.disputeRaiser = disputeRaiser;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
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

}
