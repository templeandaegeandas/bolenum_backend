/**
 * 
 */
package com.bolenum.services.user.transactions;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionTimeoutException;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import com.bolenum.constant.BTCUrlConstant;

import com.bolenum.enums.TransactionType;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.util.CryptoUtil;
import com.bolenum.util.EthereumServiceUtil;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Value("${bolenum.ethwallet.location}")
	private String ethWalletLocation;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TransactionRepo transactionRepo;

	/**
	 * to perform in app transaction for ethereum
	 * 
	 * @param fromUser
	 * @param toAddress
	 * @param txAmount
	 * @return true/false if transaction success return true else false
	 */
	@Override
	public boolean performEthTransaction(User fromUser, String toAddress, Double amount) {
		String passwordKey = fromUser.getEthWalletPwdKey();
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Credentials credentials = null;
		File walletFile = new File(ethWalletLocation + fromUser.getEthWalletJsonFileName());
		try {
			String decrPwd = CryptoUtil.decrypt(fromUser.getPassword(), passwordKey);
			credentials = WalletUtils.loadCredentials(decrPwd, walletFile);
			TransactionReceipt transactionReceipt = Transfer.sendFunds(web3j, credentials, toAddress,
					BigDecimal.valueOf(amount), Convert.Unit.ETHER);
			String txHash = transactionReceipt.getTransactionHash();
			logger.debug("transaction hash:{} for user: {}, amount: {}", txHash, fromUser.getEmailId(), amount);
			Transaction transaction = transactionRepo.findByTxHash(txHash);
			if (transaction == null) {
				transaction = new Transaction();
				transaction.setTxHash(transactionReceipt.getTransactionHash());
				transaction.setFromAddress(fromUser.getEthWalletaddress());
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				if (saved != null) {
					logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
					return true;
				}

			}
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e1) {
			e1.printStackTrace();
		} catch (IOException | InterruptedException | TransactionTimeoutException | CipherException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * to perform in app transaction for bitcoin
	 * 
	 * @param fromUser
	 * @param toAddress
	 * @param txAmount
	 * @return true/false if transaction success return true else false
	 */
	@Override
	public boolean performBtcTransaction(User fromUser, String toAddress, Double amount) {
		RestTemplate restTemplate = new RestTemplate();
		String url = BTCUrlConstant.CREATE_TX;
		MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<String, String>();
		parametersMap.add("walletId", fromUser.getBtcWalletUuid());
		parametersMap.add("transactionTradeAmount", String.valueOf(amount));
		parametersMap.add("receiverAddress", toAddress);
		parametersMap.add("transactionFee", null);
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = restTemplate.postForObject(url, parametersMap, Map.class);
			boolean isError = (boolean) map.get("error");
			logger.debug("create transaction isError:  {}", isError);
			if (isError) {
				return false;
			}
			String txHash = (String) map.get("data");
			logger.debug("transaction hash: {}", txHash);
			Transaction transaction = transactionRepo.findByTxHash(txHash);
			if (transaction == null) {
				transaction = new Transaction();
				transaction.setTxHash(txHash);
				transaction.setFromAddress(fromUser.getBtcWalletUuid());
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				if (saved != null) {
					logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
					return true;
				}
			}
		} catch (RestClientException e) {
			logger.error("btc transaction exception:  {}", e.getMessage());
			e.printStackTrace();
		}

		return false;
	}
}
