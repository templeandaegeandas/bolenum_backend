/*@Description Of class
 * 
 * CountryAndStateServiceImpl class is responsible for below listed task: 
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.Countries;
import com.bolenum.model.States;
import com.bolenum.repo.common.CountriesRepo;
import com.bolenum.repo.common.StatesRepo;

@Service
public class CountryAndStateServiceImpl implements CountryAndStateService {
	
	@Autowired
	private CountriesRepo countriesRepo;
	
	@Autowired
	private StatesRepo statesRepo;
	
	
	/**@description use to get CountriesList
	 * @return      list of countries
	 */
	@Override
	public List<Countries> getCountriesList() {
		return countriesRepo.findAll();
	}
	
	/**@description use to get state by country
	 * @param       countryId
	 * @return      List
	 */
	@Override
	public List<States> getStatesByCountry(Long countryId) {
		return statesRepo.findByCountryId(countryId);
	}
	
	/**@description use to count countries
	 * @return      No of countries (countryCount)
	 */
	@Override
	public Long countCountries() {
		return countriesRepo.count();
	}
	
	/**@description use to count states
	 * @return      No of state (stateCount)
	 */
	@Override
	public Long countStates() {
		return statesRepo.count();
	}
	
	/**@description use to save countries
	 * @param       List<Countries>
	 */
	@Override
	public void saveCountries(List<Countries> list) {
		countriesRepo.save(list);
	}
	
	
	/**@description use to save states
	 * @param     List<States>
	 */
	@Override
	public void saveStates(List<States> list) {
		statesRepo.save(list);
	}
}
