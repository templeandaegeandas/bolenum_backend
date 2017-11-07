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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.Transactional;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
import com.bolenum.enums.TransactionStatus;
import com.bolenum.enums.TransactionType;
import com.bolenum.model.Error;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.services.user.ErrorService;
import com.bolenum.services.user.notification.NotificationService;
import com.bolenum.services.user.wallet.BTCWalletService;
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

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private BTCWalletService bTCWalletService;
	
	@Autowired
	private ErrorService errorService;

	/**
	 * to perform in app transaction for ethereum
	 * 
	 * @param fromUser
	 * @param toAddress
	 * @param txAmount
	 * @return true/false if transaction success return true else false
	 */
	@Override
	public boolean performEthTransaction(User fromUser, String toAddress, Double amount,TransactionStatus transactionStatus) {

		String passwordKey = fromUser.getEthWalletPwdKey();
		logger.debug("password key: {}", passwordKey);
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Credentials credentials = null;
		String fileName = ethWalletLocation + fromUser.getEthWalletJsonFileName();
		logger.debug("user eth wallet file name: {}", fileName);
		File walletFile = new File(fileName);
		try {
			String decrPwd = CryptoUtil.decrypt(fromUser.getEthWalletPwd(), passwordKey);
			logger.debug("decr password: {}", decrPwd);
			logger.debug("ETH transaction credentials load started");
			credentials = WalletUtils.loadCredentials(decrPwd, walletFile);
			logger.debug("ETH transaction credentials load completed");
			logger.debug("ETH transaction send fund started");
			TransactionReceipt transactionReceipt = null;
			try {
				transactionReceipt = Transfer.sendFunds(web3j, credentials, toAddress,
						BigDecimal.valueOf(amount), Convert.Unit.ETHER);
			}
			catch (RuntimeException e) {
				Error error = new Error(fromUser.getEthWalletaddress(), toAddress, e.getMessage(), "ETH", amount, false);
				errorService.saveError(error);
				logger.debug("error saved: {}", error);
				return false;
			}
			logger.debug("ETH transaction send fund completed");
			String txHash = transactionReceipt.getTransactionHash();
			logger.debug("eth transaction hash:{} of user: {}, amount: {}", txHash, fromUser.getEmailId(), amount);
			Transaction transaction = transactionRepo.findByTxHash(txHash);
			logger.debug("transaction by hash: {}", transaction);
			if (transaction == null) {
				transaction = new Transaction();
				transaction.setTxHash(transactionReceipt.getTransactionHash());
				transaction.setFromAddress(fromUser.getEthWalletaddress());
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				transaction.setTransactionStatus(transactionStatus);
				transaction.setUser(fromUser);
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				if (saved != null) {
					logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
					return true;
				}

			}
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e1) {
			logger.error("ETH transaction failed:  {}", e1.getMessage());
			e1.printStackTrace();
		} catch (IOException | InterruptedException | TransactionTimeoutException | CipherException e) {
			logger.error("ETH transaction failed:  {}", e.getMessage());
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
	public boolean performBtcTransaction(User fromUser, String toAddress, Double amount,TransactionStatus transactionStatus) {
		RestTemplate restTemplate = new RestTemplate();
		String url = BTCUrlConstant.CREATE_TX;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject request = new JSONObject();
		try {
			request.put("walletId", fromUser.getBtcWalletUuid());
			request.put("transactionTradeAmount", String.valueOf(amount));
			request.put("receiverAddress", toAddress);
		} catch (JSONException e1) {
			logger.error("json parse error: {}", e1.getMessage());
			e1.printStackTrace();
		}
		HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
		try {
			ResponseEntity<String> txRes = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			System.out.println("txRes ====== ==="+txRes);
			if (txRes.getStatusCode() == HttpStatus.OK) {
				JSONObject responseJson = new JSONObject(txRes.getBody());
				logger.debug("json object of response: {}", responseJson);
				JSONObject data = (JSONObject) responseJson.get("data");
				String txHash = (String) data.get("transactionHash");
				logger.debug("transaction hash: {}", txHash);
				Transaction transaction = transactionRepo.findByTxHash(txHash);
				if (transaction == null) {
					transaction = new Transaction();
					transaction.setTxHash(txHash);
					transaction.setFromAddress(fromUser.getBtcWalletUuid());
					transaction.setToAddress(toAddress);
					transaction.setTxAmount(amount);
					transaction.setTransactionType(TransactionType.OUTGOING);
					transaction.setUser(fromUser);
					transaction.setTransactionStatus(transactionStatus);
					Transaction saved = transactionRepo.saveAndFlush(transaction);
					if (saved != null) {
						logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
						return true;
					}
				}
			}
		} catch (JSONException e) {
			logger.error("btc transaction JSON exception:  {}", e.getMessage());
			e.printStackTrace();
		} catch (RestClientException e) {
			logger.error("btc transaction exception:  {}", e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Async
	@Override
	public boolean performTransaction(String currencyAbr, double qtyTraded, User buyer, User seller) {
		String msg = "Hi " + seller.getFirstName() + ", Your transaction of selling "+qtyTraded+" "+currencyAbr+" have been processed successfully!";
		String msg1 = "Hi " + buyer.getFirstName() + ", Your transaction of buying "+qtyTraded+" "+currencyAbr+" have been processed successfully!";
		switch (currencyAbr) {
		case "BTC":
			logger.debug("BTC transaction started");
			boolean status = performBtcTransaction(seller, bTCWalletService.getWalletAddress(buyer.getBtcWalletUuid()), qtyTraded,null);
			logger.debug("is BTC transaction successed: {}", status);
//			String msg = "Hi " + seller.getFirstName() + ", Your transaction of selling "+qtyTraded+" BTC have been processed successfully!";
//			String msg1 = "Hi " + buyer.getFirstName() + ", Your transaction of buying "+qtyTraded+" BTC have been processed successfully!";
			notificationService.sendNotification(seller, msg);
			notificationService.saveNotification(buyer, seller, msg);
			notificationService.sendNotification(buyer, msg1);
			notificationService.saveNotification(buyer, seller, msg1);
			logger.debug("Message : {}",msg);
			logger.debug("Message : {}",msg1);
			return status;
		case "ETH":
			logger.debug("ETH transaction started");
			status = performEthTransaction(seller, buyer.getEthWalletaddress(), qtyTraded,null);
			logger.debug("is ETH transaction successed: {}", status);
//			String msg2 = "Hi " + seller.getFirstName() + ", Your transaction of selling "+qtyTraded+" ETH have been processed successfully!";
//			String msg3 = "Hi " + buyer.getFirstName() + ", Your transaction of buying "+qtyTraded+" ETH have been processed successfully!";
			notificationService.sendNotification(seller, msg);
			notificationService.saveNotification(buyer, seller, msg);
			notificationService.sendNotification(buyer, msg1);
			notificationService.saveNotification(buyer, seller, msg1);
			logger.debug("Message : {}",msg);
			logger.debug("Message : {}",msg1);
			return status;
		}
		return false;
	}
}
