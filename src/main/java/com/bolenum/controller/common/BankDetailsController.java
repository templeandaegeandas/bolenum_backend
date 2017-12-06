package com.bolenum.controller.common;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.AddUserBankDetailsForm;
import com.bolenum.dto.common.EditUserBankDetailsForm;
import com.bolenum.model.BankAccountDetails;
import com.bolenum.model.User;
import com.bolenum.services.common.BankAccountDetailsService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.user.UserService;
import com.bolenum.util.ErrorCollectionUtil;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;

/**
 * @Author Himanshu
 * @Date 22-Sep-2017
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
@Api(value = "Bank Details Controller")
public class BankDetailsController {

	@Autowired
	private BankAccountDetailsService bankDetailsService;

	@Autowired
	private LocaleService localService;

	@Autowired
	private UserService userService;

	public static final Logger logger = LoggerFactory.getLogger(BankDetailsController.class);

	/**
	 * to add bank details by user
	 * 
	 * @param addUserBankDetailsForm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = UrlConstant.ADD_USER_BANK_DETAILS, method = RequestMethod.POST)
	public ResponseEntity<Object> addUserBankDetails(@Valid @RequestBody AddUserBankDetailsForm addUserBankDetailsForm,
			BindingResult result) {

		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(result),
					ErrorCollectionUtil.getErrorMap(result));
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			String requestObj = mapper.writeValueAsString(addUserBankDetailsForm);
			logger.debug("Requested Object: {}", requestObj);
		} catch (JsonProcessingException e) {
			return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
					localService.getMessage("message.error"), null);
		}
		User user = GenericUtils.getLoggedInUser();
		List<BankAccountDetails> listOfBankAccountDetails = bankDetailsService.findByUser(user);
		if (listOfBankAccountDetails.size() == 2) {
			return ResponseHandler.response(HttpStatus.CONFLICT, true,
					localService.getMessage("message.bank.details.add.limit.reached"), null);
		}
		BankAccountDetails isUserBankDetailsExist = bankDetailsService
				.findByAccountNumber(addUserBankDetailsForm.getAccountNumber());
		if (isUserBankDetailsExist == null && listOfBankAccountDetails.size() <= 2) {
			BankAccountDetails bankAccountDetails = addUserBankDetailsForm.copy(new BankAccountDetails());
			bankAccountDetails.setUser(user);
			BankAccountDetails responseBankAccountDetails = bankDetailsService.saveBankDetails(bankAccountDetails);
			if (responseBankAccountDetails != null) {
				return ResponseHandler.response(HttpStatus.OK, false,
						localService.getMessage("message.bank.details.add.success"),
						bankAccountDetails.getAccountNumber());
			} else {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("message.bank.details.add.error"), null);
			}
		} else {
			return ResponseHandler.response(HttpStatus.CONFLICT, true,
					localService.getMessage("bank details of user already exist"), null);
		}
	}

	/**
	 * used for update the bank details by user
	 * 
	 * @return
	 */
	@RequestMapping(value = UrlConstant.EDIT_USER_BANK_DETAILS, method = RequestMethod.PUT)
	public ResponseEntity<Object> editUserBankDetails(
			@Valid @RequestBody EditUserBankDetailsForm editUserBankDetailsForm, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(result),
					ErrorCollectionUtil.getErrorMap(result));
		} else {
			BankAccountDetails isUserBankDetailsExist = bankDetailsService.findByID(editUserBankDetailsForm.getId());
			if (isUserBankDetailsExist != null) {

				BankAccountDetails response = bankDetailsService.updateUserBankDetails(editUserBankDetailsForm,
						isUserBankDetailsExist);
				if (response != null) {
					return ResponseHandler.response(HttpStatus.OK, false,
							localService.getMessage("message.bank.details.update.success"),
							response.getAccountNumber());
				} else {
					return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
							localService.getMessage("message.error"), null);
				}
			} else {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("message.bank.details.update.error"), null);
			}
		}

	}

	/**
	 * to view user bank details used by admin as well as user
	 * 
	 * @return
	 */

	@RequestMapping(value = UrlConstant.VIEW_USER_BANK_DETAILS, method = RequestMethod.GET)
	public ResponseEntity<Object> viewUserBankDetails() {
		User user = GenericUtils.getLoggedInUser();
		if (user.getRole().getName().equals("ROLE_USER")) {
			List<BankAccountDetails> listOfBankAccountDetails = bankDetailsService.findByUser(user);
			if (listOfBankAccountDetails != null) {
				return ResponseHandler.response(HttpStatus.OK, false,
						localService.getMessage("message.bank.details.found"), listOfBankAccountDetails);
			} else {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("message.bank.details.not.found"), null);
			}
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("user.not.found"), null);

	}

	/**
	 * to view user bank details by admin
	 * 
	 * @param id
	 * @return
	 * 
	 */
	@RequestMapping(value = UrlConstant.VIEW_USER_BANK_DETAILS_BY_ADMIN, method = RequestMethod.GET)
	public ResponseEntity<Object> viewUserBankDetailsByAdmin(@RequestParam Long userId) {
		User user = GenericUtils.getLoggedInUser();
		if (user.getRole().getName().equals("ROLE_ADMIN")) {
			User userBankDetails = userService.findByUserId(userId);
			List<BankAccountDetails> listOfBankAccountDetails = bankDetailsService.findByUser(userBankDetails);
			if (listOfBankAccountDetails != null) {
				return ResponseHandler.response(HttpStatus.OK, false,
						localService.getMessage("message.bank.details.found"), listOfBankAccountDetails);
			} else {
				return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
						localService.getMessage("message.bank.details.not.found"), null);
			}
		}
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localService.getMessage("user.not.found"), null);
	}
	
	
}
