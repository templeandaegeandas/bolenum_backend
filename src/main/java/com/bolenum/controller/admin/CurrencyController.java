/*@Description Of Class
 * 
 * CurrencyController class is responsible for below listed task: 
 *   
 *     To add new currency 
 *     To edit currency
 *     Get currency list
 *     Get currency list for admin
 *     Get currency list for market
 *     To get currency by Id
 *     To save BLNNGN Price
 */


package com.bolenum.controller.admin;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.ErrorCollectionUtil;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;

/**
 * 
 * 
 * @Author Himanshu
 * @Date 05-Oct-2017
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_ADMIN_URI_V1)
@Api(value = "Admin Controller")
public class CurrencyController {

	public static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

	@Autowired
	private LocaleService localService;

	@Autowired
	private CurrencyService currencyService;

	private static final  String CURRENCY_LIST_SUCCESS = "currency.list.success";

	/**@description use to add currency 
	 * 
	 * @param currencyForm
	 * @param result
	 * @return currency.add.success OR currency.add.failure
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.CURRENCY_FOR_TRADING, method = RequestMethod.POST)
	public ResponseEntity<Object> addCurrency(@Valid @RequestBody CurrencyForm currencyForm, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(result),
					ErrorCollectionUtil.getErrorMap(result));
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			String requestObj = mapper.writeValueAsString(currencyForm);
			logger.debug("Requested Object: {}", requestObj);
		} catch (JsonProcessingException e) {
			return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
					localService.getMessage("message.error"), null);
		}
		User user = GenericUtils.getLoggedInUser();
		if (!user.getRole().getName().equals("ROLE_ADMIN")) {
			return ResponseHandler.response(HttpStatus.CONFLICT, true,
					localService.getMessage("user.not.authorized.error"), null);
		}
		Currency existingCurrency = currencyService.findByCurrencyName(currencyForm.getCurrencyName());
		if (existingCurrency == null) {
			Currency currency = currencyForm.copy(new Currency());
			Currency responseOfSavedCurrency = currencyService.saveCurrency(currency);
			if (responseOfSavedCurrency != null) {
				return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("currency.add.success"),
						responseOfSavedCurrency);
			} else {
				return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
						localService.getMessage("currency.add.failure"), null);
			}
		} else {
			return ResponseHandler.response(HttpStatus.CONFLICT, true,
					localService.getMessage("currency.already.exist"), null);
		}

	}

	/**@description Use to edit currency
	 * 
	 * @param currencyForm
	 * @param result
	 * @return currency.update.success OR currency.not.found
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.CURRENCY_FOR_TRADING, method = RequestMethod.PUT)
	public ResponseEntity<Object> editCurrency(@Valid @RequestBody CurrencyForm currencyForm, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(result),
					ErrorCollectionUtil.getErrorMap(result));
		}
		User user = GenericUtils.getLoggedInUser();
		if (!user.getRole().getName().equals("ROLE_ADMIN")) {
			return ResponseHandler.response(HttpStatus.CONFLICT, true,
					localService.getMessage("user.not.authorized.error"), null);
		}
		Currency isExistingCurrency = currencyService.findByCurrencyName(currencyForm.getCurrencyName());

		if (isExistingCurrency != null) {
			Currency responseOfUpdatedCurrency = currencyService.updateCurrency(currencyForm, isExistingCurrency);

			if (responseOfUpdatedCurrency != null) {
				return ResponseHandler.response(HttpStatus.OK, false,
						localService.getMessage("currency.update.success"), responseOfUpdatedCurrency);
			} else {
				return ResponseHandler.response(HttpStatus.OK, false,
						localService.getMessage("currency.update.failure"), responseOfUpdatedCurrency);
			}
		} else {
			return ResponseHandler.response(HttpStatus.OK, true, localService.getMessage("currency.not.found"), null);
		}
	}

	/**
	 * @description Use to get currency list
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @param searchData
	 * @return currencyList
	 */
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@RequestMapping(value = UrlConstant.CURRENCY_LIST_FOR_TRADING, method = RequestMethod.GET)
	public ResponseEntity<Object> getCurrencyList() {
		List<Currency> currencyList = currencyService.getCurrencyList();
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage(CURRENCY_LIST_SUCCESS),
				currencyList);
	}
	/**
	 * @description Use to get currency list for admin
	 * @return currencyList
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.CURRENCY_LIST_FOR_ADMIN, method = RequestMethod.GET)
	public ResponseEntity<Object> getCurrencyListForAdmin() {
		List<Currency> currencyList = currencyService.getCurrencyListForAdmin();
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage(CURRENCY_LIST_SUCCESS),
				currencyList);
	}

	/** @description Use to get currency list for market
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @param searchData
	 * @return currencyList
	 */
	@RequestMapping(value = UrlConstant.CURRENCY_LIST_FOR_MARKET, method = RequestMethod.GET)
	public ResponseEntity<Object> getCurrencyListForMarket() {
		List<Currency> currencyList = currencyService.getCurrencyList();
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage(CURRENCY_LIST_SUCCESS),
				currencyList);
	}

	/**@description Use to get currency by Id
	 * 
	 * @param currencyId
	 * @return currency
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.CURRENCY_FOR_TRADING, method = RequestMethod.GET)
	public ResponseEntity<Object> getCurrencyById(@RequestParam Long currencyId) {
		Currency currency = currencyService.findCurrencyById(currencyId);
		if (currency != null) {
			return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("currency.found.success"),
					currency);
		} else {
			return ResponseHandler.response(HttpStatus.CONFLICT, false, localService.getMessage("currency.not.found"),
					null);
		}
	}
	/**@description Use to save BLNNGN Price
	 * 
	 * @param currencyId
	 * @return savedCurrency
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.CURRENCY_NGN_PRICE_SAVE, method = RequestMethod.PUT)
	public ResponseEntity<Object> saveBLNNGNPrice(@RequestParam("priceNGN") double priceNGN) {
		Currency currency = currencyService.findByCurrencyAbbreviation("BLN");
		Currency savedCurrency = currencyService.saveCurrency(currency);
		return ResponseHandler.response(HttpStatus.OK, false, localService.getMessage("currency.price.saved"),
				savedCurrency);
	}
}
