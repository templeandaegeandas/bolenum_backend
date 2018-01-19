 /*@Description Of Class
 * 
 * CurrencyPairController class is responsible for below listed task: 
 * 
 *     Create currency pair
 *     Get currency pair list  
 */


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
import com.bolenum.enums.CurrencyType;
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

	/**@description Use to create currency pair
	 * 
	 * @param currencyId
	 * @param pairedCurrencyId
	 * @return currency pair add success
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.CURRENCY_PAIR, method = RequestMethod.POST)
	public ResponseEntity<Object> createCurrencyPair(
			@RequestParam("marketCurrencyAbbreviation") String marketCurrencyAbbreviation,
			@RequestParam("pairedCurrencyAbbreviation") String pairedCurrencyAbbreviation) {
		Currency marketCurrency = currencyService.findByCurrencyAbbreviation(marketCurrencyAbbreviation);
		Currency pairedCurrency = currencyService.findByCurrencyAbbreviation(pairedCurrencyAbbreviation);
		if (marketCurrency == null || pairedCurrency == null) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("currency.pair.not.valid"), Optional.empty());
		}
		if (CurrencyType.FIAT.equals(marketCurrency.getCurrencyType())) {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("You can't create NGN market"), Optional.empty());
		}
		if (marketCurrency.equals(pairedCurrency)) {
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

	/**@description Use to get currency pair list
	 * 
	 * @param currencyId
	 * @param pairedCurrencyId
	 * @return currencyPairList
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.CURRENCY_PAIR_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getCurrencyPairList() {
		List<Currency> currencyPairList = currencyService.getCurrencyListWithPair();
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("currency.pair.list.success"),
				currencyPairList);
	}
}