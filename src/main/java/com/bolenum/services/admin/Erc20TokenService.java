package com.bolenum.services.admin;

import org.springframework.data.domain.Page;

import com.bolenum.model.Erc20Token;

/**
 * 
 * @author Vishal Kumar
 * @date 04-Oct-2017
 *
 */
public interface Erc20TokenService {

	/**
	 * 
	 * @param erc20Token
	 * @return Erc20Token
	 */
	Erc20Token saveToken(Erc20Token erc20Token);

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @param searchData
	 * @return Page
	 */
	Page<Erc20Token> listAllErc20Token(int pageNumber, int pageSize, String sortBy, String sortOrder);

	/**
	 * 
	 * @param id
	 * @return Erc20Token
	 */
	Erc20Token getById(Long id);

	/**
	 * 
	 * @return Erc20Token
	 */
	Erc20Token saveBolenumErc20Token();

}
