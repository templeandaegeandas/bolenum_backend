package com.bolenum.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.UpdateTimestamp;

import com.bolenum.enums.DocumentStatus;
import com.bolenum.enums.DocumentType;

/**
 * 
 * @author Vishal Kumar
 * @date 19-sep-2017
 *
 */

@Entity
public class UserKyc {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Enumerated(EnumType.STRING)
	private DocumentType documentType;
	private String document;

	@Enumerated(EnumType.STRING)
	private DocumentStatus documentStatus = DocumentStatus.SUBMITTED;
	private String rejectionMessage;

	@UpdateTimestamp
	private Date uploadedDate;
	private Date verifiedDate;
	private Boolean isVerified = false;

	@ManyToOne
	private User user;



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
	 * @return the documentType
	 */
	public DocumentType getDocumentType() {
		return documentType;
	}

	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	/**
	 * @return the document
	 */
	public String getDocument() {
		return document;
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(String document) {
		this.document = document;
	}

	/**
	 * @return the documentStatus
	 */
	public DocumentStatus getDocumentStatus() {
		return documentStatus;
	}

	/**
	 * @param documentStatus the documentStatus to set
	 */
	public void setDocumentStatus(DocumentStatus documentStatus) {
		this.documentStatus = documentStatus;
	}

	/**
	 * @return the rejectionMessage
	 */
	public String getRejectionMessage() {
		return rejectionMessage;
	}

	/**
	 * @param rejectionMessage the rejectionMessage to set
	 */
	public void setRejectionMessage(String rejectionMessage) {
		this.rejectionMessage = rejectionMessage;
	}

	/**
	 * @return the uploadedDate
	 */
	public Date getUploadedDate() {
		return uploadedDate;
	}

	/**
	 * @param uploadedDate the uploadedDate to set
	 */
	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	/**
	 * @return the verifiedDate
	 */
	public Date getVerifiedDate() {
		return verifiedDate;
	}

	/**
	 * @param verifiedDate the verifiedDate to set
	 */
	public void setVerifiedDate(Date verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	/**
	 * @return the isVerified
	 */
	public Boolean getIsVerified() {
		return isVerified;
	}

	/**
	 * @param isVerified the isVerified to set
	 */
	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentType == null) ? 0 : documentType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserKyc other = (UserKyc) obj;
		return (documentType == other.documentType);
	}
}
