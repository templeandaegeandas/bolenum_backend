package com.bolenum.services.common.coin;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.data.domain.Page;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;

import com.bolenum.model.User;
import com.bolenum.model.coin.Erc20Token;
import com.bolenum.model.coin.UserCoin;

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

//	Double getErc20WalletBalance(User user, Erc20Token erc20Token);

	TransactionReceipt transferErc20Token(User user, Erc20Token erc20Token, String toAddress, Double fund)
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException, CipherException, TransactionException, InterruptedException,
			ExecutionException;

	void saveIncomingErc20Transaction(String tokenName) throws IOException, CipherException;


	Erc20Token getByCoin(String coin);

	void createErc20Wallet(User user, String tokenName);

	UserCoin erc20WalletBalance(User user, Erc20Token erc20Token);

	void sendUserTokenToAdmin();

	Double getErc20WalletBalance(User user, Erc20Token erc20Token, String tokenName);
}
