package com.bolenum.repo.common;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.enums.CurrencyType;
import com.bolenum.model.Currency;

public interface CurrencyRepo extends JpaRepository<Currency, Long> {

	/**
	 * This method is use to find Currency By CurrencyName Or CurrencyAbbreviationLike
	 * @param searchData
	 * @param pageable
	 * @return
	 */
	Page<Currency> findByCurrencyNameOrCurrencyAbbreviationLike(String searchData, Pageable pageable);

	/**
	 * This method is use to find Currency By CurrencyId
	 * @param currencyId
	 * @return
	 */
	Currency findByCurrencyId(Long currencyId);

	/**
	 * This method is use to find currency By Currency Abbreviation
	 * @param currencyAbbreviation
	 * @return
	 */
	Currency findByCurrencyAbbreviation(String currencyAbbreviation);

	/**
	 * This method is use to find currency by CurrencyNameInIgnoreCase
	 * @param currencyName
	 * @return
	 */
	Currency findByCurrencyNameInIgnoreCase(String currencyName);

	/**
	 * This method is use to find currency by CurrencyAbbreviation InIgnoreCase
	 * @param currencyAbbreviation
	 * @return
	 */
	Currency findByCurrencyAbbreviationInIgnoreCase(String currencyAbbreviation);

	/**
	 * This method is use to find currency by CurrencyType Not In
	 * @param type
	 * @return
	 */
	List<Currency> findByCurrencyTypeNotIn(CurrencyType type);

	/**
	 * This method is use to find currency by Market Is NotNull
	 * @return
	 */
	List<Currency> findByMarketIsNotNull();
}
