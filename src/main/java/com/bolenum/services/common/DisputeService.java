package com.bolenum.services.common;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.bolenum.enums.DisputeStatus;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.MobileNotVerifiedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.DisputeOrder;

/**
 * 
 * @author Himanshu Kumar
 *
 */
public interface DisputeService {

	DisputeOrder uploadProofDocument(MultipartFile file, DisputeOrder disputeOrder, User user)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException;

	Boolean checkExpiryToDispute(Long orderId);

	DisputeOrder raiseDispute(Long orderId, Long transactionid, String comment, MultipartFile file)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException;

	Boolean isAlreadyDisputed(Long orderId, Long transactionId);

	Boolean checkEligibilityToDispute(Long orderId);

	Page<DisputeOrder> getListOfDisputeOrder(int pageNumber, int pageSize, String sortBy, String sortOrder,
			DisputeStatus disputeStatus);

	DisputeOrder getDisputeOrderByID(Long disputeId);

	DisputeOrder performActionOnRaisedDispute(DisputeOrder disputeOrder, String commentByAdmin,
			DisputeStatus disputeStatus);
	
	void sendDisputeNotification(DisputeOrder disputeOrder, User disputeRaiser,User disputeRaisedAgainst);

}
