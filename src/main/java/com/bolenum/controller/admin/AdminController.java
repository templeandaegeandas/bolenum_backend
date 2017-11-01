package com.bolenum.controller.admin;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.AddTransactioFeeAndLimitForm;
import com.bolenum.model.TransactionFee;
import com.bolenum.model.User;
import com.bolenum.services.admin.AdminService;
import com.bolenum.services.admin.TransactionFeeService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * @Author Himanshu Kumar
 *
 * @Date 05-Sep-2017
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_ADMIN_URI_V1)
@Api(value = "Admin Controller")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private LocaleService localeService;

	@Autowired
	private TransactionFeeService transactionFeeService;

	public static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@RequestMapping()
	public ResponseEntity<Object> index() {
		return null;
	}

	/**
	 * to get list of all the users enrolled in system
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @param searchData
	 * @return
	 */
	@RequestMapping(value = UrlConstant.LIST_USERS, method = RequestMethod.GET)
	public ResponseEntity<Object> getUsersList(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortBy") String sortBy,
			@RequestParam("sortOrder") String sortOrder, @RequestParam("searchData") String searchData) {
		User user = GenericUtils.getLoggedInUser();
		Page<User> userList = adminService.getUsersList(pageNumber, pageSize, sortBy, sortOrder, searchData, user);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("admin.user.list"), userList);
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = UrlConstant.GET_USER_BY_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getUsersById(@PathVariable("userId") Long userId) {
		User user = adminService.getUserById(userId);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("admin.user.get.by.id"), user);
	}

	/**
	 * to add transaction fees for transaction done by user and deducted fees will
	 * be store in Admin wallet
	 * 
	 * @param transactionFee
	 * @param result
	 * @return
	 */
	@RequestMapping(value = UrlConstant.TRANSACTION_FEES, method = RequestMethod.POST)
	public ResponseEntity<Object> addTransactionFees(@Valid @RequestBody AddTransactioFeeAndLimitForm addTransactioFeeAndLimitForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("admin.transactionfee.add.error"), null);
		} else {
			TransactionFee savedTransactionFee = transactionFeeService.saveTransactionFee(addTransactioFeeAndLimitForm);
			if (savedTransactionFee != null) {
				return ResponseHandler.response(HttpStatus.OK, false,
						localeService.getMessage("admin.trnsactionfee.add.success"), savedTransactionFee);
			}
			return ResponseHandler.response(HttpStatus.FORBIDDEN, true,
					localeService.getMessage("admin.transactionfee.add.error"), null);
		}
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = UrlConstant.TRANSACTION_FEES, method = RequestMethod.GET)
	public ResponseEntity<Object> getTransactionFees() {
		// List<TransactionFee> listOfTransactionFee=
		return ResponseHandler.response(HttpStatus.OK, true,
				localeService.getMessage("admin.transaction.fees.found.success"), null);
	}
}
