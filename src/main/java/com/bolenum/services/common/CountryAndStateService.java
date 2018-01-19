/*@Description Of interface
 * 
 * CountryAndStateService interface is responsible for below listed task: 
 * 
 * 		Get countriesList
 *      Get statesByCountry
 *      Count countries
 *      Count states
 *      Save countries
 *      Save states
 */

package com.bolenum.services.common;

import java.util.List;

import com.bolenum.model.Countries;
import com.bolenum.model.States;

public interface CountryAndStateService {

	/**@description use to get CountriesList
	 * @return      list of countries
	 */
	List<Countries> getCountriesList();

	/**@description use to get state by country
	 * @param       countryId
	 * @return      List
	 */
	List<States> getStatesByCountry(Long countryId);

	/**@description use to count countries
	 * @return      Long
	 */
	Long countCountries();

	/**@description use to count states
	 * @return      Long
	 */
	Long countStates();

	/**@description use to save countries
	 * @return      Long
	 */
	void saveCountries(List<Countries> list);

	/**@description use to save states
	 * @return      list of states
	 */
	void saveStates(List<States> list);

}
