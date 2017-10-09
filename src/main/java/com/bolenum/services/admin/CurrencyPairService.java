package com.bolenum.services.admin;

import org.springframework.data.domain.Page;

import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;

/**
 * 
 * @Author Himanshu
 * @Date 09-Oct-2017
 */
public interface CurrencyPairService {

	public CurrencyPair saveCurrencyPair(CurrencyPair existingCurrencyPair);

	public CurrencyPair findByCurrencyPairName(String currencyPairName);

	public String createCurrencyPairName(Currency toCurrency, Currency pairedCurrency);

	public CurrencyPair findByCurrencyPairNameByReverse(String currencyPairName);

	public Page<Currency> getCurrencyList(int pageNumber, int pageSize, String sortBy, String sortOrder,
			String searchData);

	public Boolean validCurrencyPair(CurrencyPair currencyPair);
	
	
}
