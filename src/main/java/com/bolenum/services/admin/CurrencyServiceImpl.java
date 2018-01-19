/*@Description Of CurrencyServiceImpl
 * 
 * CurrencyServiceImpl class is responsible for below listed task: 
 *      
 *      Find by currency name
 *      Save currency
 *      Update currency
 *      Find currency by id
 *      Get currency list
 *      Get currency list by name
 *      Count currencies
 *      Find by currency abbreviation
 *      Get currency list for admin
 *      Create pair
 *      Get currency list with pair
 **/

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

	
	/**@description use to get Currency by name
	 * @param       currencyName
	 * @return      Currency
	 */
	@Override
	public Currency findByCurrencyName(String currencyName) {
		return currencyRepo.findByCurrencyNameInIgnoreCase(currencyName);
	}
	


	/**@description use to find Currency Abbreviation
	 * @param       currencyAbbreviation
	 * @return      Currency 
	 */
	@Override
	public Currency findByCurrencyAbbreviation(String currencyAbbreviation) {
		return currencyRepo.findByCurrencyAbbreviationInIgnoreCase(currencyAbbreviation);
	}
	
	
	/**@description use to save Currency
	 * @param       currency
	 * @return      Currency
	 */
	@Override
	public Currency saveCurrency(Currency currency) {

		return currencyRepo.saveAndFlush(currency);
	}

	/**@description use to update Currency
	 * @param       currencyForm
	 * @param       isExistingCurrency
	 * @return      Currency
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
	
	/**@description      use to get Currency list
	 * @return           Currency list
	 */
	@Override
	public List<Currency> getCurrencyList() {
		return currencyRepo.findByCurrencyTypeNotIn(CurrencyType.FIAT);
	}
	
	
	/**@description use to get Currency list for admin
	 *@return       Currency list
	 */
	@Override
	public List<Currency> getCurrencyListForAdmin() {
		return currencyRepo.findAll();
	}

	/**@description use to find Currency by Id
	 * @param       currencyId
	 * @return      Currency
	 */
	@Override
	public Currency findCurrencyById(Long currencyId) {
		return currencyRepo.findByCurrencyId(currencyId);
	}

	/**@description use to count Currency
	 * @return       Currency count 
	 */
	@Override
	public long countCourencies() {
		return currencyRepo.count();
	}
	
	/**@description use to find Currency by name
	 * @param           tokenName
	 * @return          Currency list
	 */
	@Override
	public List<Currency> getCurrencyListByName(String tokenName) {
		Currency currency = currencyRepo.findByCurrencyAbbreviation(tokenName);
		List<Currency> list = new ArrayList<>();
		list.add(currency);
		return list;
	}
	
	/**@description use to create pair
	 * @param       marketCurrency
	 * @param       pairedCurrency
	 * @return      Currency 
	 */
	@Override
	public Currency createPair(Currency marketCurrency, Currency pairedCurrency) {
		if (marketCurrency.getMarket().contains(pairedCurrency)
				|| pairedCurrency.getMarket().contains(marketCurrency)) {
			return null;
		}
		List<Currency> newPairList = new ArrayList<>();
		newPairList.add(pairedCurrency);
		marketCurrency.setMarket(newPairList);
		return currencyRepo.save(marketCurrency);
	}
	/**@description use to get Currency list with pair
	 *@return       Currency list
	 */
	@Override
	public List<Currency> getCurrencyListWithPair() {
		return currencyRepo.findByMarketIsNotNull();
	}
}
