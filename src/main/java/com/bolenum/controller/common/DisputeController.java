package com.bolenum.controller.common;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolenum.constant.UrlConstant;
import com.bolenum.controller.user.UserController;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.MobileNotVerifiedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.DisputeOrder;
import com.bolenum.services.common.DisputeService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * 
 * @author Himanshu Kumar
 *
 */

@RestController
@Api(value = "Dispute Controller")
public class DisputeController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private LocaleService localeService;

	@Autowired
	private DisputeService disputeService;

	/**
	 * 
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = UrlConstant.RAISE_DISPUTE, method = RequestMethod.POST)
	public ResponseEntity<Object> requestDisputeOrder(@RequestParam Long orderId, @RequestParam Long transactionId,
			@RequestParam String comment) {

		Boolean isEligible = disputeService.checkEligibilityToDispute(orderId);
		if (isEligible) {

			Boolean isExistDisputeOrder = disputeService.isAlreadyDisputed(orderId, transactionId);
			if (!isExistDisputeOrder) {
				
				return ResponseHandler.response(HttpStatus.CONFLICT, true, localeService.getMessage("dispute.already.raised"),
						null);

			}
			DisputeOrder response = disputeService.raiseDispute(orderId, transactionId, comment);

			if (response != null) {
				return ResponseHandler.response(HttpStatus.OK, true, localeService.getMessage("dispute.raised.succes"),
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
	@RequestMapping(value = UrlConstant.UPLOAD_PROOF_DOCUMENT_FOR_DISPUTE, method = RequestMethod.POST)
	public ResponseEntity<Object> uploadProofDocumentForDispute(@RequestParam("disputeId") Long disputeId,
			@RequestParam("file") MultipartFile file, @RequestParam String documentType)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException {

		User user = GenericUtils.getLoggedInUser();
		DisputeOrder response = disputeService.uploadProofDocument(file, disputeId, user.getUserId());
		if (response != null) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("dispute.proof.uploaded.success"), response);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("dispute.proof.uploaded.failed"), null);
		}
	}

}
