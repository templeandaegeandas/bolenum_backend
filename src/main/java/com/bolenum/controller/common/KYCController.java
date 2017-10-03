package com.bolenum.controller.common;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bolenum.constant.UrlConstant;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.User;
import com.bolenum.model.UserKyc;
import com.bolenum.services.common.KYCService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

/**
 * 
 * @Author Vishal Kumar
 * @Date 03-Oct-2017
 */

@RestController
public class KYCController {

	@Autowired
	private KYCService kycService;
	@Autowired
	private LocaleService localeService;

	public static final Logger logger = LoggerFactory.getLogger(KYCController.class);

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws PersistenceException
	 * @throws MaxSizeExceedException
	 */
	@RequestMapping(value = UrlConstant.UPLOAD_DOCUMENT, method = RequestMethod.POST)
	public ResponseEntity<Object> uploadKycDocument(@RequestParam("file") MultipartFile file)
			throws IOException, PersistenceException, MaxSizeExceedException {
		User user = GenericUtils.getLoggedInUser();
		User response = kycService.uploadKycDocument(file, user.getUserId());
		if (response != null) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("user.document.uploaded.success"), response);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("user.document.uploaded.failed"), null);
		}
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = UrlConstant.APPROVE_DOCUMENT, method = RequestMethod.PUT)
	public ResponseEntity<Object> approveKycDocument(@PathVariable("userId") Long userId) {
		User user = kycService.approveKycDocument(userId);
		if (user != null) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("user.document.disapprove.success"), user);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("user.document.disapprove.failed"), null);
		}
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping(value = UrlConstant.DISAPPROVE_DOCUMENT, method = RequestMethod.PUT)
	public ResponseEntity<Object> disApproveKycDocument(@RequestBody Map<String, String> data) {
		User user = kycService.disApprovedKycDocument(Long.parseLong(data.get("userId")), data.get("rejectionMessage"));
		if (user != null) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("user.document.approve.success"), user);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("user.document.approve.failed"), null);
		}
	}

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @param searchData
	 * @return
	 */
	@RequestMapping(value = UrlConstant.SUBMITTED_KYC_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getSubmittedKycList(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortBy") String sortBy,
			@RequestParam("sortOrder") String sortOrder, @RequestParam("searchData") String searchData) {
		Page<User> kycList = kycService.getSubmitedKycList(pageNumber, pageSize, sortBy, sortOrder, searchData);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("submitted.kyc.list"), kycList);
	}

	/**
	 * 
	 * @param kycId
	 * @return
	 */
	@RequestMapping(value = UrlConstant.GET_KYC_BY_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getKycById(@PathVariable("kycId") Long kycId) {
		UserKyc userKyc = kycService.getUserKycById(kycId);
		if (userKyc != null) {
			return ResponseHandler.response(HttpStatus.OK, false,
					localeService.getMessage("user.kyc.get.by.id.success"), userKyc);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("user.kyc.get.by.id.failed"), null);
		}
	}
}
