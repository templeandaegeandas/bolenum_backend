package com.bolenum.repo.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.CurrencyPair;

/**
 * 
 * @Author Himanshu
 * @Date 09-Oct-2017
 */
public interface CurrencyPairRepo extends JpaRepository<CurrencyPair, Long> {

	CurrencyPair findByPairName(String currencyPairName);

//	Page<Currency> findByCurrencyPairNameOrCurrencyAbbreviationLike(String searchData, Pageable pageRequest);

}
