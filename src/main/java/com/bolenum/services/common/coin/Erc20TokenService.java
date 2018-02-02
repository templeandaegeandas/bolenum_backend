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
	 * This method is use to save Token
	 * @param erc20Token
	 * @return Erc20Token
	 */
	Erc20Token saveToken(Erc20Token erc20Token);

	/**
	 * This method is use to list All Erc20 Token
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @param searchData
	 * @return Page
	 */
	Page<Erc20Token> listAllErc20Token(int pageNumber, int pageSize, String sortBy, String sortOrder);

	/**
	 * This method is use to get Erc20Token ById
	 * @param id
	 * @return Erc20Token
	 */
	Erc20Token getById(Long id);

	/**
	 * This method is use to save Initial Erc20 Token
	 * @param erc20Tokens
	 */
	void saveInitialErc20Token(List<Erc20Token> erc20Tokens);

	/**
	 * This method is use to count Erc20 Token
	 * @return Long
	 */
	Long countErc20Token();

	/**
	 * This method is use to save Incoming Erc20 Transaction
	 * @param tokenName
	 * @throws IOException
	 * @throws CipherException
	 */
	void saveIncomingErc20Transaction(String tokenName) throws IOException, CipherException;

	/**
	 * This method is use to get Erc20Token by coin
	 * @param coin
	 * @return
	 */
	Erc20Token getByCoin(String coin);

	/**
	 * This method is use to create Erc20 Wallet
	 * @param user
	 * @param tokenName
	 */
	void createErc20Wallet(User user, String tokenName);

	/**
	 * This method is use for erc20 Wallet Balance
	 * @param user
	 * @param erc20Token
	 * @return
	 */
	UserCoin erc20WalletBalance(User user, Erc20Token erc20Token);

	/**
	 * This method is use to send User Token To Admin
	 */
	void sendUserTokenToAdmin();

	/**
	 * This method is use to get Erc20 Wallet Balance
	 * @param user
	 * @param erc20Token
	 * @param tokenName
	 * @return
	 */
	Double getErc20WalletBalance(User user, Erc20Token erc20Token, String tokenName);

	/**
	 * This method is use to get Erc20 Wallet Balance Temp
	 * @param user
	 * @param erc20Token
	 * @return
	 */
	Double getErc20WalletBalanceTemp(User user, Erc20Token erc20Token);

	/**
	 * This method is use to transfer Erc20 Token
	 * @param user
	 * @param erc20Token
	 * @param toAddress
	 * @param fund
	 * @param tokenName
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 * @throws CipherException
	 * @throws TransactionException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	TransactionReceipt transferErc20Token(User user, Erc20Token erc20Token, String toAddress, Double fund,
			String tokenName) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, IOException, CipherException, TransactionException,
			InterruptedException, ExecutionException;

	// void sendUserTokenToAdminTemp()
	// throws IOException, InvalidKeyException, NoSuchAlgorithmException,
	// NoSuchPaddingException,
	// IllegalBlockSizeException, BadPaddingException, CipherException,
	// InterruptedException, TransactionException, ExecutionException;
}
