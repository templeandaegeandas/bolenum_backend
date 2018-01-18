package com.bolenum.services.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.enums.CurrencyType;
import com.bolenum.model.Currency;
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
		return currencyRepo.findByCurrencyTypeNotIn(CurrencyType.FIAT);
	}

	@Override
	public List<Currency> getCurrencyListForAdmin() {
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
	 * to count records
	 */
	@Override
	public long countCourencies() {
		return currencyRepo.count();
	}

	@Override
	public List<Currency> getCurrencyListByName(String tokenName) {
		Currency currency = currencyRepo.findByCurrencyAbbreviation(tokenName);
		List<Currency> list = new ArrayList<>();
		list.add(currency);
		return list;
	}

	@Override
	public Currency createPair(Currency marketCurrency, Currency pairedCurrency) {
		if (marketCurrency.getMarket().contains(pairedCurrency)
				|| pairedCurrency.getMarket().contains(marketCurrency)) {
			return null;
		}
		List<Currency> newPairList = marketCurrency.getMarket();
		newPairList.add(pairedCurrency);
		marketCurrency.setMarket(newPairList);
		return currencyRepo.save(marketCurrency);
	}
	
	@Override
	public List<Currency> getCurrencyListWithPair() {
		return currencyRepo.findByMarketIsNotNull();
	}
}
