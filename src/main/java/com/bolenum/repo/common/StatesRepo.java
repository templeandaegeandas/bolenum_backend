package com.bolenum.repo.common;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.States;

public interface StatesRepo extends JpaRepository<States, Long>{

	List<States> findByCountryId(Long countryid);
}
