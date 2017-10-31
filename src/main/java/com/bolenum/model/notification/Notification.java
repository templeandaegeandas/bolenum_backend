/**
 * 
 */
package com.bolenum.model.notification;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import com.bolenum.model.User;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chandan kumar singh
 * @date 31-Oct-2017
 */
@Entity
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private User sender;
	private User receiver;
	private String message;
	private boolean readStatus;

	@ApiModelProperty(hidden = true)
	@CreationTimestamp
	private Date createdOn;

	@ApiModelProperty(hidden = true)
	private Date deletedOn;

	private boolean isDeleted;

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
	 * @return the sender
	 */
	public User getSender() {
		return sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public void setSender(User sender) {
		this.sender = sender;
	}

	/**
	 * @return the receiver
	 */
	public User getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the readStatus
	 */
	public boolean isReadStatus() {
		return readStatus;
	}

	/**
	 * @param readStatus the readStatus to set
	 */
	public void setReadStatus(boolean readStatus) {
		this.readStatus = readStatus;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
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
	 * @param deletedOn the deletedOn to set
	 */
	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
}
