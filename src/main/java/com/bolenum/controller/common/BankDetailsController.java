package com.bolenum.controller.common;

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
import org.springframework.web.bind.annotation.RestController;
import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.AddUserBankDetailsForm;
import com.bolenum.model.BankAccountDetails;
import com.bolenum.model.User;
import com.bolenum.services.common.BankDetailsService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.ErrorCollectionUtil;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @Author himanshu
 * @Date 22-Sep-2017
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
public class BankDetailsController {

	@Autowired
	private BankDetailsService bankDetailsService;

	@Autowired
	private LocaleService localService;

	public static final Logger logger = LoggerFactory.getLogger(BankDetailsController.class);

	@RequestMapping(value = UrlConstant.ADD_USER_BANK_DETAILS, method = RequestMethod.POST)
	public ResponseEntity<Object> addUserBankDetails(@Valid @RequestBody AddUserBankDetailsForm addUserBankDetailsForm,
			BindingResult result) {
		User user = GenericUtils.getLoggedInUser();
		if (result.hasErrors() && user == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(result),
					ErrorCollectionUtil.getErrorMap(result));
		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				String requestObj = mapper.writeValueAsString(addUserBankDetailsForm);
				logger.debug("Requested Object:" + requestObj);

				BankAccountDetails isUserBankDetailsExist = bankDetailsService
						.findByAccountNumber(addUserBankDetailsForm.getAccountNumber());
				if (isUserBankDetailsExist == null) {
					BankAccountDetails bankAccountDetails = addUserBankDetailsForm.copy(new BankAccountDetails());
					bankAccountDetails.setUser(user);
					BankAccountDetails responseBankAccountDetails = bankDetailsService
							.saveBankDetails(bankAccountDetails);
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
			} catch (JsonProcessingException e) {
				return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
						localService.getMessage("message.error"), null);
			}
		}

	}

	@RequestMapping(value = UrlConstant.EDIT_USER_BANK_DETAILS, method = RequestMethod.POST)
	public ResponseEntity<Object> editUserBankDetails() {
		User user = GenericUtils.getLoggedInUser();
		return null;
	}

	@RequestMapping(value = UrlConstant.VIEW_USER_BANK_DETAILS, method = RequestMethod.POST)
	public ResponseEntity<Object> viewUserBankDetails() {
		User user = GenericUtils.getLoggedInUser();
		return null;
	}

}
