package com.bolenum.controller.common;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolenum.constant.UrlConstant;
import com.bolenum.controller.user.UserController;
import com.bolenum.enums.DisputeStatus;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.MobileNotVerifiedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.DisputeOrder;
import com.bolenum.services.common.DisputeService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * 
 * @author Himanshu Kumar
 *
 */

@RestController
@Api(value = "Dispute Controller")
@RequestMapping(value = UrlConstant.BASE_URI_V1)
public class DisputeController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private LocaleService localeService;

	@Autowired
	private DisputeService disputeService;

	/**
	 * 
	 * @param orderId
	 * @param transactionId
	 * @param file
	 * @param comment
	 * @return
	 * @throws IOException
	 * @throws PersistenceException
	 * @throws MaxSizeExceedException
	 * @throws MobileNotVerifiedException
	 */
	@RequestMapping(value = UrlConstant.RAISE_DISPUTE, method = RequestMethod.POST)
	public ResponseEntity<Object> requestDisputeOrder(@RequestParam Long orderId, @RequestParam(required = false) Long transactionId,
			@RequestParam("file") MultipartFile file, @RequestParam String comment)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException {

		Boolean isEligible = disputeService.checkEligibilityToDispute(orderId);

		if (!isEligible) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("dispute.not.eligible"), null);
		}

		Boolean isExpired = disputeService.checkExpiryToDispute(orderId);

		if (!isExpired) {
			Boolean isExistDisputeOrder = disputeService.isAlreadyDisputed(orderId, transactionId);
			if (!isExistDisputeOrder) {
				return ResponseHandler.response(HttpStatus.CONFLICT, true,
						localeService.getMessage("dispute.already.raised"), null);
			}

			DisputeOrder response = disputeService.raiseDispute(orderId, transactionId, comment, file);

			// DisputeOrder responseOfProofUpload =disputeService.uploadProofDocument(file,
			// response.getId(), user.getUserId());
			if (response != null) {
				return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("dispute.raised.succes"),
						response);
			}
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("dispute.time.not.eligible"), null);
		}

		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("dispute.raised.failed"),
				null);

	}

	/**
	 * 
	 * @param disputeId
	 * @param file
	 * @param documentType
	 * @return
	 * @throws IOException
	 * @throws PersistenceException
	 * @throws MaxSizeExceedException
	 * @throws MobileNotVerifiedException
	 * 
	 */

	/*
	 * @RequestMapping(value = UrlConstant.UPLOAD_PROOF_DOCUMENT_FOR_DISPUTE, method
	 * = RequestMethod.POST) public ResponseEntity<Object>
	 * uploadProofDocumentForDispute(@RequestParam("disputeId") Long disputeId,
	 * 
	 * @RequestParam("file") MultipartFile file, @RequestParam String documentType)
	 * throws IOException, PersistenceException, MaxSizeExceedException,
	 * MobileNotVerifiedException {
	 * 
	 * User user = GenericUtils.getLoggedInUser(); DisputeOrder response =
	 * disputeService.uploadProofDocument(file, disputeId, user.getUserId()); if
	 * (response != null) { return ResponseHandler.response(HttpStatus.OK, false,
	 * localeService.getMessage("dispute.proof.uploaded.success"), response); } else
	 * { return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
	 * localeService.getMessage("dispute.proof.uploaded.failed"), null); } }
	 */

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @return
	 */
	@RequestMapping(value = UrlConstant.RAISED_DISPUTE_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getRaisedDisputeOrderList(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortBy") String sortBy,
			@RequestParam("sortOrder") String sortOrder, @RequestParam("disputeStatus") DisputeStatus disputeStatus) {
		Page<DisputeOrder> listOfDisputeOrder = disputeService.getListOfDisputeOrder(pageNumber, pageSize, sortBy,
				sortOrder, disputeStatus);
		return ResponseHandler.response(HttpStatus.OK, true, localeService.getMessage("dispute.order.submitted.list"),
				listOfDisputeOrder);
	}

	/**
	 * 
	 * @param disputeId
	 * @return
	 */
	@RequestMapping(value = UrlConstant.RAISED_DISPUTE_ORDER, method = RequestMethod.GET)
	public ResponseEntity<Object> getRaisedDisputeOrder(@RequestParam("disputeId") Long disputeId) {
		DisputeOrder disputeOrder = disputeService.getDisputeOrderByID(disputeId);
		if (disputeOrder != null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, false,
					localeService.getMessage("dispute.order.found.success"), disputeOrder);
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
				localeService.getMessage("dispute.order.not.found"), null);
	}

	/**
	 * 
	 * @param disputeId
	 * @param commentForDisputeRaiser
	 * @param commentForDisputeRaisedAgainst
	 * @param disputeStatus
	 * @return
	 */
	@RequestMapping(value = UrlConstant.ACTION_ON_RAISED_DISPUTE_ORDER, method = RequestMethod.POST)
	public ResponseEntity<Object> actionOnRaisedDispute(@RequestParam("disputeId") Long disputeId,
			@RequestParam("commentForDisputeRaiser") String commentForDisputeRaiser,
			@RequestParam("commentForDisputeRaisedAgainst") String commentForDisputeRaisedAgainst,
			@RequestParam("disputeStatus") DisputeStatus disputeStatus) {

		DisputeOrder disputeOrder = disputeService.getDisputeOrderByID(disputeId);

		if (disputeOrder == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("dispute.order.not.found"), null);
		}

		if (DisputeStatus.COMPLETED.equals(disputeOrder.getDisputeStatus())) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("dispute.order.already.completed"), null);
		}

		DisputeOrder response = disputeService.performActionOnRaisedDispute(disputeOrder, commentForDisputeRaiser,
				commentForDisputeRaisedAgainst, disputeStatus);

		if (response != null) {
			User disputeRaiser = response.getDisputeRaiser();
			User disputeRaisedAgainst = response.getDisputeRaisedAgainst();
			disputeService.sendDisputeNotification(response, disputeRaiser, disputeRaisedAgainst);
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("dispute.order.action.success"), response);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("dispute.order.action.failed"), null);
		}
	}

}
