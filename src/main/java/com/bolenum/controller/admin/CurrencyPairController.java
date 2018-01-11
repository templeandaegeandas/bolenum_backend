package com.bolenum.controller.admin;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.model.Currency;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.ResponseHandler;

import io.swagger.annotations.Api;

/**
 * 
 * @Author himanshu
 * @Date 18-Oct-2017
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_ADMIN_URI_V1)
@Api(value = "Currency Pair Controller")
public class CurrencyPairController {

	@Autowired
	private LocaleService localeService;

	@Autowired
	private CurrencyService currencyService;

	public static final Logger logger = LoggerFactory.getLogger(CurrencyPairController.class);

	/**
	 * 
	 * @param currencyId
	 * @param pairedCurrencyId
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.CURRENCY_PAIR, method = RequestMethod.POST)
	public ResponseEntity<Object> createCurrencyPair(@RequestParam("marketCurrencyId") long currencyId,
			@RequestParam("pairedCurrencyId") long pairedCurrencyId) {
		Currency marketCurrency = currencyService.findCurrencyById(currencyId);
		Currency pairedCurrency = currencyService.findCurrencyById(pairedCurrencyId);
		if (marketCurrency == null || pairedCurrency == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("currency.pair.not.valid"), Optional.empty());
		}
		Currency newPair = currencyService.createPair(marketCurrency, pairedCurrency);
		if (newPair != null) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("currency.pair.add.success"),
					newPair);
		}
		return ResponseHandler.response(HttpStatus.CONFLICT, true,
				localeService.getMessage("currency.pair.already.exist"), Optional.empty());
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.CURRENCY_PAIR_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getCurrencyPairList() {
		List<Currency> currencyPairList = currencyService.getCurrencyListWithPair();
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("currency.pair.list.success"),
				currencyPairList);
	}
}