package com.bolenum.services.common;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.MobileNotVerifiedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.orders.book.DisputeOrder;

public interface DisputeService {

	DisputeOrder uploadProofDocument(MultipartFile file, Long disputeId, Long userId)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException;

	Boolean checkEligibilityToDispute(Long orderId);

	DisputeOrder raiseDispute(Long orderId, Long transactionid, String comment);

	Boolean isAlreadyDisputed(Long orderId , Long transactionId);

}
