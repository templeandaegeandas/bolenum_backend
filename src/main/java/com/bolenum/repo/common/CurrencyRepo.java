package com.bolenum.repo.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.Currency;

public interface CurrencyRepo extends JpaRepository<Currency,Long>{


	Page<Currency> findByCurrencyNameOrCurrencyAbbreviationLike(String searchData, Pageable pageable );

	Currency findByCurrencyId(Long currencyId);

	Currency findByCurrencyAbbreviation(String currencyAbbreviation);

	Currency findByCurrencyNameInIgnoreCase(String currencyName);
	
//	@Query("select c from Currency c Left Join c.CurrencyPair")
//	List<Currency> getBypairNotNull();
	
}
