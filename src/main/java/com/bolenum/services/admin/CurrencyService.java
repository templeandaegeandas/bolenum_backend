/*@Description Of interface
 * 
 * CurrencyService interface is responsible for below listed task: 
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

import java.util.List;

import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.model.Currency;

/**
 * 
 * @Author Himanshu
 * @Date 09-Oct-2017
 */
public interface CurrencyService {

	/**@description use to get Currency by name
	 * @param       currencyName
	 * @return      Currency
	 */
	public Currency findByCurrencyName(String currencyName);
	
	
	/**@description use to save Currency
	 * @param       currency
	 * @return      Currency
	 */
	public Currency saveCurrency(Currency currency);
	
	/**@description use to update Currency
	 * @param       currencyForm
	 * @param       isExistingCurrency
	 * @return      Currency
	 */
	public Currency updateCurrency(CurrencyForm currencyForm, Currency isExistingCurrency);
	
	
	/**@description use to find Currency by Id
	 * @param       currencyId
	 * @return      Currency
	 */
	public Currency findCurrencyById(Long currencyId);

	/**@description      use to get Currency list
	 * @return           Currency list
	 */
	List<Currency> getCurrencyList();
	
	/**@description use to find Currency by name
	 * @param           tokenName
	 * @return          Currency list
	 */
	List<Currency> getCurrencyListByName(String tokenName);

	
	/**@description use to count Currency
	 * @return       Currency count 
	 */
	public long countCourencies();
	
	/**@description use to find Currency Abbreviation
	 * @param       currencyAbbreviation
	 * @return      Currency 
	 */
	Currency findByCurrencyAbbreviation(String currencyAbbreviation);
	
	
	/**@description use to get Currency list for admin
	 *@return       Currency list
	 */
	List<Currency> getCurrencyListForAdmin();

	
	/**@description use to create pair
	 * @param       marketCurrency
	 * @param       pairedCurrency
	 * @return      Currency 
	 */
	Currency createPair(Currency marketCurrency, Currency pairedCurrency);
	
	/**@description use to get Currency list with pair
	 *@return       Currency list
	 */
	List<Currency> getCurrencyListWithPair();
}
