package com.bolenum.services.admin;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.model.Currency;
import com.bolenum.repo.common.CurrencyRepo;
/**
 * 
 * @Author himanshu
 * @Date 06-Oct-2017
 */
@Service
public class CurrencyServiceImpl implements CurrencyService {

	@Autowired
	private CurrencyRepo currencyRepo;

	@Override
	public Currency findByCurrencyName(String currencyName) {
		return currencyRepo.findByCurrencyName(currencyName);
	}

	@Override
	public Currency saveCurrency(Currency currency) {
		return currencyRepo.saveAndFlush(currency);
	}

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
	public Page<Currency> getCurrencyList(int pageNumber, int pageSize, String sortBy, String sortOrder,
			String searchData) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return currencyRepo.findByCurrencyNameOrCurrencyAbbreviationLike(searchData, pageRequest);
	}
/**
 * 
 */
	@Override
	public Currency findCurrencyById(Long currencyId) {
		return currencyRepo.findByCurrencyId(currencyId);
	}

}
