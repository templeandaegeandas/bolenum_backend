
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
import com.bolenum.model.orders.book.Orders;

/**
 * 
 * @author Himanshu Kumar
 *
 */
public interface DisputeService {

	/**
	 * This method is use to upload proof document
	 * @param file
	 * @param disputeOrder
	 * @param user
	 * @return
	 * @throws IOException
	 * @throws PersistenceException
	 * @throws MaxSizeExceedException
	 * @throws MobileNotVerifiedException
	 */
	DisputeOrder uploadProofDocument(MultipartFile file, DisputeOrder disputeOrder, User user)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException;

	/**
	 * This method is use to check Expiry To Dispute
	 * @param orders
	 * @return Boolean
	 */
	Boolean checkExpiryToDispute(Orders orders);

	// DisputeOrder raiseDispute(Orders orders, Long transactionid, String comment,
	// MultipartFile file)
	// throws IOException, PersistenceException, MaxSizeExceedException,
	// MobileNotVerifiedException;

	/**
	 * This method is use to isAlready Disputed
	 * @param orders
	 * @param transactionId
	 * @return Boolean
	 */
	Boolean isAlreadyDisputed(Orders orders, Long transactionId);

	/**
	 * This method is use to check Eligibility To Dispute
	 * @param orderId
	 * @return Orders
	 */
	Orders checkEligibilityToDispute(Long orderId);

	/**
	 * This method is use to get List Of Dispute Order
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @return DisputeOrder
	 */
	Page<DisputeOrder> getListOfDisputeOrder(int pageNumber, int pageSize, String sortBy, String sortOrder);

	/**
	 * This method is use to get Dispute Order ByID
	 * @param disputeId
	 * @return Dispute Order
	 */
	DisputeOrder getDisputeOrderByID(Long disputeId);

	/**
	 * This method is use to send Dispute Notification
	 * @param disputeOrder
	 * @param disputeRaiser
	 * @param disputeRaisedAgainst
	 */
	void sendDisputeNotification(DisputeOrder disputeOrder, User disputeRaiser, User disputeRaisedAgainst);

	/**
	 * Tgis method is use to perform Action On Raised Dispute
	 * @param disputeOrder
	 * @param commentForDisputeRaiser
	 * @param commentForDisputeRaisedAgainst
	 * @param disputeStatus
	 * @return DisputeOrder
	 */
	DisputeOrder performActionOnRaisedDispute(DisputeOrder disputeOrder, String commentForDisputeRaiser,
			String commentForDisputeRaisedAgainst, DisputeStatus disputeStatus);

	/**
	 * This method is use to raise Dispute By Seller
	 * @param order
	 * @return DisputeOrder
	 */
	DisputeOrder raiseDisputeBySeller(Orders order);

	/**
	 * Tgis method is use to raise Dispute By Buyer
	 * @param orders
	 * @param transactionId
	 * @param commentByDisputeRaiser
	 * @param file
	 * @return DisputeOrder
	 * @throws IOException
	 * @throws PersistenceException
	 * @throws MaxSizeExceedException
	 * @throws MobileNotVerifiedException
	 */
	DisputeOrder raiseDisputeByBuyer(Orders orders, Long transactionId, String commentByDisputeRaiser,
			MultipartFile file)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException;;

}
