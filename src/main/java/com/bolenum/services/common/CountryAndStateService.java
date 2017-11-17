package com.bolenum.services.common;

import java.util.List;

import com.bolenum.model.Countries;
import com.bolenum.model.States;

public interface CountryAndStateService {

	/**
	 * 
	 * @return List
	 */
	List<Countries> getCountriesList();

	/**
	 * 
	 * @param countryId
	 * @return List
	 */
	List<States> getStatesByCountry(Long countryId);

	/**
	 * 
	 * @return Long
	 */
	Long countCountries();

	/**
	 * 
	 * @return Long
	 */
	Long countStates();

	/**
	 * 
	 * @param list
	 */
	void saveCountries(List<Countries> list);

	/**
	 * 
	 * @param list
	 */
	void saveStates(List<States> list);

}
