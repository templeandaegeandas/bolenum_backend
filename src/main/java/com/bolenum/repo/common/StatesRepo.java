package com.bolenum.repo.common;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.States;

public interface StatesRepo extends JpaRepository<States, Long>{

	/**
	 * This method is use to find States by country id
	 * @param countryid
	 * @return
	 */
	List<States> findByCountryId(Long countryid);
}
