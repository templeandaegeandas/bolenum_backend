package com.bolenum.repo.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.States;
import java.lang.Long;
import java.util.List;

public interface StatesRepo extends JpaRepository<States, Long>{

	List<States> findByCountryId(Long countryid);
}
