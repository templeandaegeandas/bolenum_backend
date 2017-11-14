package com.bolenum.services.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import com.bolenum.repo.common.CurrencyRepo;

/**
 * 
 * @Author Himanshu
 * @Date 06-Oct-2017
 */
@Service
public class CurrencyServiceImpl implements CurrencyService {
	
	public static final Logger logger = LoggerFactory.getLogger(CurrencyServiceImpl.class);

	@Autowired
	private CurrencyRepo currencyRepo;

	@Autowired
	private CurrencyPairService currencyPairService;

	@Override
	public Currency findByCurrencyName(String currencyName) {
		return currencyRepo.findByCurrencyNameInIgnoreCase(currencyName);
	}
	
	@Override
	public Currency findByCurrencyAbbreviation(String currencyAbbreviation) {
		return currencyRepo.findByCurrencyAbbreviationInIgnoreCase(currencyAbbreviation);
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

	@Override
	public List<Currency> getCurrencyListForMarket() {
		List<CurrencyPair> allCurrencyPair = currencyPairService.findAllCurrencyPair();
		List<Currency> allCurrencies = currencyRepo.findAll();
		Iterator<CurrencyPair> iterator = allCurrencyPair.iterator();
		List<Currency> listOfToCurrency = new ArrayList<Currency>();
		while (iterator.hasNext()) {
			listOfToCurrency.add(iterator.next().getToCurrency().get(0));
		}
		allCurrencies.retainAll(listOfToCurrency);
		return allCurrencies;
	}

	/**
	 * 
	 */
	@Override
	public Currency findCurrencyById(Long currencyId) {
		return currencyRepo.findByCurrencyId(currencyId);
	}

	/**
	 * to count records
	 */
	@Override
	public long countCourencies() {
		return currencyRepo.count();
	}

	@Override
	public List<Currency> getCurrencyListByName(String tokenName) {
		Currency currency = currencyRepo.findByCurrencyAbbreviation(tokenName);
		List<Currency> list = new ArrayList<Currency>();
		list.add(currency);
		return list;
	}

}
