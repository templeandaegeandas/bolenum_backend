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
	
	@Override
	public List<Countries> getCountriesList() {
		return countriesRepo.findAll();
	}
	
	@Override
	public List<States> getStatesByCountry(Long countryId) {
		return statesRepo.findByCountryId(countryId);
	}
	
	@Override
	public Long countCountries() {
		return countriesRepo.count();
	}
	
	@Override
	public Long countStates() {
		return statesRepo.count();
	}
	
	@Override
	public void saveCountries(List<Countries> list) {
		countriesRepo.save(list);
	}
	
	@Override
	public void saveStates(List<States> list) {
		statesRepo.save(list);
	}
}
