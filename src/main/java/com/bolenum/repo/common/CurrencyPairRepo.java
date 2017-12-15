package com.bolenum.repo.common;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.CurrencyPair;

/**
 * @Author Himanshu
 * @Date 09-Oct-2017
 */
public interface CurrencyPairRepo extends JpaRepository<CurrencyPair, Long> {

	CurrencyPair findByPairName(String currencyPairName);

	CurrencyPair findByPairId(Long pairId);

	Page<CurrencyPair> findByIsEnabled(Boolean isEnabled, Pageable page);

	List<CurrencyPair> findByToCurrencyCurrencyIdAndIsEnabled(Long currencyId, Boolean isEnabled);

}
