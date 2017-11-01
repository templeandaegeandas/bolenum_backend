package com.bolenum.services.admin;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.data.domain.Page;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.bolenum.model.Erc20Token;
import com.bolenum.model.User;

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
	 * @param erc20Tokens
	 */
	void saveInitialErc20Token(List<Erc20Token> erc20Tokens);
	
	/**
	 * 
	 * @return Long
	 */
	Long countErc20Token();
	
	Double getErc20WalletBalance(User user, String tokenName) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, CipherException, InterruptedException, ExecutionException;


	Erc20Token saveBolenumErc20Token();

	Future<TransactionReceipt> transferErc20Token(User user, String tokenName, String toAddress, Double fund)
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException, CipherException;

}
