package com.bolenum.repo.common.coin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.coin.Erc20Token;

/**
 * 
 * @author Vishal Kumar
 * @date 04-Oct-2017
 *
 */
public interface Erc20TokenRepository extends JpaRepository<Erc20Token, Long> {

	/**
	 * This method is use find ERC20Token by is deleted
	 * @param searchDate
	 * @param pageable
	 * @return Page<Erc20Token>
	 */
	Page<Erc20Token> findByIsDeleted(Boolean isDeleted, Pageable pageable);
	/**
	 * This method is use to find Erc20Token by contract address
	 * @param contractaddress
	 * @return Erc20Token
	 */
	Erc20Token findByContractAddress(String contractaddress);
	
	/**
	 * This method is use to find Erc20Token by currency abbreviation
	 * @param currency
	 * @return Erc20Token
	 */
	Erc20Token findByCurrencyCurrencyAbbreviation(String currency);
}
