package com.bolenum.controller.admin;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.CurrencyPairForm;
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.User;
import com.bolenum.services.admin.CurrencyPairService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.ErrorCollectionUtil;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = UrlConstant.BASE_ADMIN_URI_V1)
@Api(value = "Currency Pair Controller")
public class CurrencyPairController {

	@Autowired
	private LocaleService localeService;

	public static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private CurrencyPairService currencyPairService;

	@RequestMapping(value = UrlConstant.CURRENCY_PAIR, method = RequestMethod.POST)
	public ResponseEntity<Object> createCurrencyPair(@Valid @RequestBody CurrencyPairForm currencyPairForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ErrorCollectionUtil.getError(result),
					ErrorCollectionUtil.getErrorMap(result));
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			String requestObj = mapper.writeValueAsString(currencyPairForm);
			logger.debug("Requested Object: {}", requestObj);
		} catch (JsonProcessingException e) {
			return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
					localeService.getMessage("message.error"), null);
		}

		User user = GenericUtils.getLoggedInUser();

		if (!user.getRole().getName().equals("ROLE_ADMIN")) {
			return ResponseHandler.response(HttpStatus.CONFLICT, true,
					localeService.getMessage("user.not.authorized.error"), null);
		}
		String currencyPairName = currencyPairService.createCurrencyPairName(currencyPairForm.getToCurrency(),
				currencyPairForm.getPairedCurrency());
		CurrencyPair existingCurrencyPair = currencyPairService.findByCurrencyPairName(currencyPairName);
		CurrencyPair existingCurrencyPairByReverse = currencyPairService
				.findByCurrencyPairNameByReverse(currencyPairName);

		if (existingCurrencyPair == null && existingCurrencyPairByReverse == null) {
			CurrencyPair currencyPair = currencyPairForm.copy(new CurrencyPair());
			Boolean isValid = currencyPairService.validCurrencyPair(currencyPair);
			// currencyPairForm.copy(new CurrencyPair());
			if (isValid) {
				CurrencyPair savedCurrencyPair = currencyPairService.saveCurrencyPair(currencyPair);
				if (savedCurrencyPair != null) {
					return ResponseHandler.response(HttpStatus.OK, false,
							localeService.getMessage("currency.pair.add.success"), savedCurrencyPair);
				} else {
					return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true,
							localeService.getMessage("currency.pair.add.failure"), null);
				}
			} else {
				return ResponseHandler.response(HttpStatus.CONFLICT, true,
						localeService.getMessage("currency.pair.not.valid"), null);
			}
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("currency.pair.already.exist"), null);
		}
	}

	@RequestMapping(value = UrlConstant.CURRENCY_PAIR, method = RequestMethod.GET)
	public ResponseEntity<Object> getCurrencyList(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortBy") String sortBy,
			@RequestParam("sortOrder") String sortOrder, @RequestParam("searchData") String searchData) {
		Page<Currency> currencyPairList = currencyPairService.getCurrencyList(pageNumber, pageSize, sortBy, sortOrder,
				searchData);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("currency.pair.list.success"),
				currencyPairList);
	}

}
