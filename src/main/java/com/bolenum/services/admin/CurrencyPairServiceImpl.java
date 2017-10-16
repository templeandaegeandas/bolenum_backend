package com.bolenum.services.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import com.bolenum.repo.common.CurrencyPairRepo;
import com.bolenum.repo.common.CurrencyRepo;

/**
 * 
 * @Author Himanshu
 * @Date 09-Oct-2017
 */
@Service
public class CurrencyPairServiceImpl implements CurrencyPairService {

	@Autowired
	private CurrencyPairRepo currencyPairRepo;

	@Autowired
	private CurrencyRepo currencyRepo;

	@Override
	public CurrencyPair findByCurrencyPairName(String currencyPairName) {
		return currencyPairRepo.findByPairName(currencyPairName);
	}

	/**
	 * 
	 */
	@Override
	public CurrencyPair saveCurrencyPair(CurrencyPair currencyPair) {
		return currencyPairRepo.saveAndFlush(currencyPair);
	}

	/**
	 * 
	 */
	@Override
	public String createCurrencyPairName(Currency toCurrency, Currency pairedCurrency) {

		return toCurrency.getCurrencyAbbreviation() + "/" + pairedCurrency.getCurrencyAbbreviation();
	}

	/**
	 * 
	 */
	@Override
	public CurrencyPair findByCurrencyPairNameByReverse(String currencyPairName) {
		String[] pairNameArray = currencyPairName.split("/");
		String pairNameByReverse = pairNameArray[1] + "/" + pairNameArray[0];
		return currencyPairRepo.findByPairName(pairNameByReverse);
	}

	/**
	 * 
	 */
	@Override
	public Page<CurrencyPair> getCurrencyList(int pageNumber, int pageSize, String sortBy, String sortOrder) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return currencyPairRepo.findByIsEnabled(true, pageRequest);

	}

	/**
	 * check for valid currency pair to add
	 */
	@Override
	public Boolean validCurrencyPair(CurrencyPair currencyPair) {
		Currency toCurrency = currencyRepo
				.findByCurrencyNameInIgnoreCase(currencyPair.getToCurrency().get(0).getCurrencyName());
		Currency pairedCurrency = currencyRepo
				.findByCurrencyNameInIgnoreCase(currencyPair.getPairedCurrency().get(0).getCurrencyName());
		Currency toCurrencyByAbbreviation = currencyRepo
				.findByCurrencyAbbreviation(currencyPair.getToCurrency().get(0).getCurrencyAbbreviation());
		Currency pairedCurrencyByAbbreviation = currencyRepo
				.findByCurrencyAbbreviation(currencyPair.getPairedCurrency().get(0).getCurrencyAbbreviation());

		if (toCurrency != null && pairedCurrency != null && toCurrencyByAbbreviation != null
				&& pairedCurrencyByAbbreviation != null
				&& toCurrency.getCurrencyId() != pairedCurrency.getCurrencyId()) {
			return true;
		}
		return false;
	}

	/** 
	 * 
	 */
	@Override
	public CurrencyPair findCurrencypairByPairId(Long pairId) {
		return currencyPairRepo.findByPairId(pairId);
	}

	/**
	 * 
	 */
	@Override
	public CurrencyPair changeStateOfCurrencyPair(CurrencyPair isCurrencyPairExist) {
		if (isCurrencyPairExist.getIsEnabled()) {
			isCurrencyPairExist.setIsEnabled(false);
			return currencyPairRepo.saveAndFlush(isCurrencyPairExist);
		} else {
			isCurrencyPairExist.setIsEnabled(true);
			return currencyPairRepo.saveAndFlush(isCurrencyPairExist);
		}
	}

	/**
	 * 
	 */
	@Override
	public CurrencyPair findCurrencypairByPairName(String pairName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<CurrencyPair> findCurrencyPairByCurrencyId(Long currencyId) {
		return currencyPairRepo.findByToCurrencyCurrencyIdAndIsEnabled(currencyId, true);
	}

}
