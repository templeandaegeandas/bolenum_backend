package com.bolenum.services.admin;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.model.Currency;
import com.bolenum.repo.common.CurrencyRepo;

/**
 * 
 * @Author Himanshu
 * @Date 06-Oct-2017
 */
@Service
public class CurrencyServiceImpl implements CurrencyService {

	@Autowired
	private CurrencyRepo currencyRepo;

	@Override
	public Currency findByCurrencyName(String currencyName) {
		return currencyRepo.findByCurrencyNameInIgnoreCase(currencyName);
	}

	@Override
	public Currency saveCurrency(Currency currency) {

		return currencyRepo.saveAndFlush(currency);
	}

	/**
	 * 
	 */
	@Override
	public Currency updateCurrency(CurrencyForm currencyForm, Currency existingCurrency) {
		if (currencyForm.getCurrencyName() != null) {
			existingCurrency.setCurrencyName(currencyForm.getCurrencyName());
		}

		if (currencyForm.getCurrencyAbbreviation() != null) {
			existingCurrency.setCurrencyAbbreviation(currencyForm.getCurrencyAbbreviation());
		}
		existingCurrency.setUpdatedOn(new Date());
		return currencyRepo.saveAndFlush(existingCurrency);
	}

	/**
	 * 
	 */
	@Override
	public List<Currency> getCurrencyList() {
		return currencyRepo.findAll();
	}

	/**
	 * 
	 */
	@Override
	public Currency findCurrencyById(Long currencyId) {
		return currencyRepo.findByCurrencyId(currencyId);
	}

	/**
	 *  to count records
	 */
	@Override
	public long countCourencies() {
		return currencyRepo.count();
	}

}
