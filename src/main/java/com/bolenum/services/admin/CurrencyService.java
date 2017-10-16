package com.bolenum.services.admin;

import java.util.List;

import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.model.Currency;

/**
 * 
 * @Author Himanshu
 * @Date 09-Oct-2017
 */
public interface CurrencyService {

	public Currency findByCurrencyName(String currencyName);

	public Currency saveCurrency(Currency currency);

	public Currency updateCurrency(CurrencyForm currencyForm, Currency isExistingCurrency);

	public Currency findCurrencyById(Long currencyId);

	List<Currency> getCurrencyList();

	public long countCourencies();
}
