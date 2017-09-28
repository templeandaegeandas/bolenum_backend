package com.bolenum.repo.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.Countries;

public interface CountriesRepo extends JpaRepository<Countries, Long>{

}
