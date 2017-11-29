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
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.transaction.Transactional;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import com.bolenum.constant.BTCUrlConstant;
import com.bolenum.constant.UrlConstant;
import com.bolenum.enums.TransactionStatus;
import com.bolenum.enums.TransactionType;
import com.bolenum.model.Currency;
import com.bolenum.model.Erc20Token;
import com.bolenum.model.Error;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.fees.WithdrawalFee;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.admin.Erc20TokenService;
import com.bolenum.services.admin.fees.WithdrawalFeeService;
import com.bolenum.services.user.ErrorService;
import com.bolenum.services.user.notification.NotificationService;
import com.bolenum.services.user.wallet.BTCWalletService;
import com.bolenum.util.CryptoUtil;
import com.bolenum.util.EthereumServiceUtil;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 * @modified Vishal Kumar
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

	@Autowired
	private Erc20TokenService erc20TokenService;

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private WithdrawalFeeService withdrawalFeeService;

	/**
	 * to perform in app transaction for ethereum
	 * 
	 * @param fromUser
	 * @param toAddress
	 * @param txAmount
	 * @return true/false if transaction success return true else false
	 */
	@Override
	@Async
	public Future<Boolean> performEthTransaction(User fromUser, String toAddress, Double amount,
			TransactionStatus transactionStatus) {

		String passwordKey = fromUser.getEthWalletPwdKey();
		logger.debug("password key: {}", passwordKey);
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Credentials credentials = null;
		String fileName = ethWalletLocation + fromUser.getEthWalletJsonFileName();
		logger.debug("user eth wallet file name: {}", fileName);
		File walletFile = new File(fileName);
		try {
			String decrPwd = CryptoUtil.decrypt(fromUser.getEthWalletPwd(), passwordKey);
			// logger.debug("decr password: {}", decrPwd);
			TransactionReceipt transactionReceipt = null;
			try {
				logger.debug("ETH transaction credentials load started");
				credentials = WalletUtils.loadCredentials(decrPwd, walletFile);
				logger.debug("ETH transaction credentials load completed");
				logger.debug("ETH transaction send fund started");
				RemoteCall<TransactionReceipt> tr = Transfer.sendFunds(web3j, credentials, toAddress,
						BigDecimal.valueOf(amount), Convert.Unit.ETHER);
				transactionReceipt = tr.send();
			} catch (Exception e) {
				Error error = new Error(fromUser.getEthWalletaddress(), toAddress, e.getMessage(), "ETH", amount,
						false);
				errorService.saveError(error);
				logger.debug("error saved: {}", error);
				return new AsyncResult<Boolean>(false);
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
				transaction.setFromUser(fromUser);
				transaction.setCurrencyName("ETH");
				User receiverUser = userRepository.findByEthWalletaddress(toAddress);
				if (receiverUser != null) {
					transaction.setToUser(receiverUser);
				}

				Transaction saved = transactionRepo.saveAndFlush(transaction);
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_WITHDRAW,
							com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
					return new AsyncResult<Boolean>(true);
				}
			}
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e1) {
			logger.error("ETH transaction failed:  {}", e1.getMessage());
			e1.printStackTrace();
		}
		return new AsyncResult<Boolean>(false);
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
	@Async
	public Future<Boolean> performBtcTransaction(User fromUser, String toAddress, Double amount,
			TransactionStatus transactionStatus) {
		Currency currency = currencyService.findByCurrencyAbbreviation("BTC");
		WithdrawalFee fee = null;
		double txFeePerKb = 0.001;
		if (currency != null) {
			fee = withdrawalFeeService.getWithdrawalFee(currency.getCurrencyId());
		}
		if (fee != null) {
			txFeePerKb = fee.getFee();
		}
		DecimalFormat df = new DecimalFormat("0");
		df.setMaximumFractionDigits(8);
		logger.debug("perform btc trnsaction with fee/KB: {}", df.format(txFeePerKb));
		RestTemplate restTemplate = new RestTemplate();
		String url = BTCUrlConstant.CREATE_TX;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject request = new JSONObject();
		try {
			request.put("walletId", fromUser.getBtcWalletUuid());
			request.put("transactionTradeAmount", String.valueOf(amount));
			request.put("receiverAddress", toAddress);
			request.put("transactionFee", txFeePerKb);
		} catch (JSONException e) {
			logger.error("json parse error: {}", e.getMessage());
			e.printStackTrace();
		}
		HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
		try {
			ResponseEntity<String> txRes = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			if (txRes.getStatusCode() == HttpStatus.OK) {
				JSONObject responseJson = new JSONObject(txRes.getBody());

				logger.debug("json object of response: {}", responseJson);
				JSONObject data = (JSONObject) responseJson.get("data");
				String txHash = (String) data.get("transactionHash");
				logger.debug("transaction hash: {}", txHash);
				String txFee = (String) data.get("transactionFee");
				logger.debug("transaction fee: {}", txFee);
				Transaction transaction = transactionRepo.findByTxHash(txHash);
				if (transaction == null) {
					transaction = new Transaction();
					transaction.setTxFee((txFee != null) ? Double.parseDouble(txFee) : 0);
					logger.debug("transaction hash inside if condition : {}", txHash);
					transaction.setTxHash(txHash);
					logger.debug(fromUser.getBtcWalletAddress());
					transaction.setFromAddress(fromUser.getBtcWalletAddress());
					transaction.setToAddress(toAddress);
					logger.debug("toAddress : {}", toAddress);
					transaction.setTxAmount(amount);
					logger.debug("amount to be transferred : {}", amount);
					transaction.setTransactionType(TransactionType.OUTGOING);
					logger.debug("TransactionType = : {}", TransactionType.OUTGOING);
					transaction.setFromUser(fromUser);
					logger.debug("amount transferred by user : {}", fromUser.getEmailId());
					transaction.setTransactionStatus(transactionStatus);
					logger.debug("transactionStatus = : {}", transactionStatus);
					transaction.setCurrencyName("BTC");
					logger.debug("Name of Currency to be transferred: {}", transaction.getCurrencyName());
					User receiverUser = userRepository.findByBtcWalletAddress(toAddress);
					if (receiverUser != null) {
						transaction.setToUser(receiverUser);

						logger.debug("receiver user email id: {}", receiverUser.getEmailId());

					}
					Transaction saved = transactionRepo.saveAndFlush(transaction);
					if (saved != null) {
						simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_WITHDRAW,
								com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
						logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
						return new AsyncResult<Boolean>(true);
					}
				} else {
					logger.debug(" transaction exist hash: {}", transaction.getTxHash());
				}
			}
		} catch (JSONException e) {
			Error error = new Error(fromUser.getBtcWalletAddress(), toAddress,
					e.getMessage() + ", ERROR: transaction completed but transaction object not saved in db", "BTC",
					amount, false);
			errorService.saveError(error);
			logger.debug("error saved: {}", error);
			logger.error("btc transaction exception:  {}", e.getMessage());
			e.printStackTrace();
		} catch (RestClientException e) {
			Error error = new Error(fromUser.getBtcWalletAddress(), toAddress, e.getMessage(), "BTC", amount, false);
			errorService.saveError(error);
			logger.debug("error saved: {}", error);
			logger.error("btc transaction exception:  {}", e.getMessage());
			e.printStackTrace();
		}
		return new AsyncResult<Boolean>(false);
	}

	@Override
	@Async
	public Future<Boolean> performErc20Transaction(User fromUser, String tokenName, String toAddress, Double amount,
			TransactionStatus transactionStatus) {
		try {
			Erc20Token erc20Token = erc20TokenService.getByCoin(tokenName);
			TransactionReceipt transactionReceipt = erc20TokenService.transferErc20Token(fromUser, erc20Token,
					toAddress, amount);
			logger.debug("{} transaction send fund completed", tokenName);
			String txHash = transactionReceipt.getTransactionHash();
			logger.debug("{} transaction hash: {} of user: {}, amount: {}", tokenName, txHash, fromUser.getEmailId(),
					amount);
			Transaction transaction = transactionRepo.findByTxHash(txHash);
			logger.debug("transaction by hash: {}", transaction);
			if (transaction == null) {
				logger.debug("saving transaction for user: {}", fromUser.getEmailId());
				transaction = new Transaction();
				transaction.setTxHash(transactionReceipt.getTransactionHash());
				transaction.setFromAddress(fromUser.getEthWalletaddress());
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				transaction.setTransactionStatus(transactionStatus);
				transaction.setFromUser(fromUser);
				transaction.setCurrencyName(tokenName);
				User receiverUser = userRepository.findByEthWalletaddress(toAddress);
				if (receiverUser != null) {
					transaction.setToUser(receiverUser);

				}
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				logger.debug("transaction saved completed: {}", fromUser.getEmailId());
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_WITHDRAW,
							com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("message sent to websocket: {}", com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					;
					logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
					return new AsyncResult<Boolean>(true);
				}
			} else {
				logger.debug("transaction else part already saved: {}", transaction.getTxHash());
				transaction.setTxHash(transactionReceipt.getTransactionHash());
				transaction.setFromAddress(fromUser.getEthWalletaddress());
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				transaction.setTransactionStatus(transactionStatus);
				transaction.setFromUser(fromUser);
				transaction.setCurrencyName(tokenName);
				User receiverUser = userRepository.findByEthWalletaddress(toAddress);
				logger.debug("receiver else part: {}", receiverUser);
				if (receiverUser != null) {
					logger.debug("receiver else part saved with user: {}", receiverUser.getUserId());
					transaction.setToUser(receiverUser);

				}
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				logger.debug("transaction else part saved completed: {}", fromUser.getEmailId());
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_WITHDRAW,
							com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("message sent to websocket: {}", com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					;
					logger.debug("transaction else part saved successfully of user: {}", fromUser.getEmailId());
					return new AsyncResult<Boolean>(true);
				}
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | IOException | CipherException | TransactionException | InterruptedException
				| ExecutionException e) {
			logger.error("{} transaction failed:  {}", tokenName, e.getMessage());
			e.printStackTrace();
		}
		return new AsyncResult<Boolean>(false);
	}

	@Override
	@Async
	public Future<Boolean> performTransaction(String currencyAbr, double qtyTraded, User buyer, User seller) {
		String currencyType = currencyService.findByCurrencyAbbreviation(currencyAbr).getCurrencyType().toString();
		String msg = "Hi " + seller.getFirstName() + ", Your transaction of selling " + qtyTraded + " " + currencyAbr
				+ " have been processed successfully!";
		String msg1 = "Hi " + buyer.getFirstName() + ", Your transaction of buying " + qtyTraded + " " + currencyAbr
				+ " have been processed successfully!";
		Future<Boolean> txStatus;
		switch (currencyType) {
		case "CRYPTO":
			switch (currencyAbr) {
			case "BTC":
				logger.debug("BTC transaction started");
				txStatus = performBtcTransaction(seller, bTCWalletService.getWalletAddress(buyer.getBtcWalletUuid()),
						qtyTraded, null);
				try {
					boolean res = txStatus.get();
					logger.debug("is BTC transaction successed: {}", res);
					if (res) {
						notificationService.sendNotification(seller, msg);
						notificationService.saveNotification(buyer, seller, msg);
						notificationService.sendNotification(buyer, msg1);
						notificationService.saveNotification(buyer, seller, msg1);
						logger.debug("Message : {}", msg);
						logger.debug("Message : {}", msg1);
						return new AsyncResult<Boolean>(res);
					}
				} catch (InterruptedException | ExecutionException e) {
					logger.error("BTC transaction failed: {}", e.getMessage());
					e.printStackTrace();
					return new AsyncResult<Boolean>(false);
				}
			case "ETH":
				logger.debug("ETH transaction started");
				txStatus = performEthTransaction(seller, buyer.getEthWalletaddress(), qtyTraded, null);
				try {
					boolean res = txStatus.get();
					logger.debug("is ETH transaction successed: {}", res);
					notificationService.sendNotification(seller, msg);
					notificationService.saveNotification(buyer, seller, msg);
					notificationService.sendNotification(buyer, msg1);
					notificationService.saveNotification(buyer, seller, msg1);
					logger.debug("Message : {}", msg);
					logger.debug("Message : {}", msg1);
					return new AsyncResult<Boolean>(res);
				} catch (InterruptedException | ExecutionException e) {
					logger.error("ETH transaction failed: {}", e.getMessage());
					e.printStackTrace();
					return new AsyncResult<Boolean>(false);
				}
			}
			break;

		case "ERC20TOKEN":
			logger.debug("ERC20TOKEN transaction started");
			txStatus = performErc20Transaction(seller, currencyAbr, buyer.getEthWalletaddress(), qtyTraded, null);
			try {
				boolean res = txStatus.get();
				logger.debug("is ERC20TOKEN transaction successed: {}", res);
				if (res) {
					notificationService.sendNotification(seller, msg);
					notificationService.saveNotification(buyer, seller, msg);
					notificationService.sendNotification(buyer, msg1);
					notificationService.saveNotification(buyer, seller, msg1);
					logger.debug("Message : {}", msg);
					logger.debug("Message : {}", msg1);
					return new AsyncResult<Boolean>(res);
				}
			} catch (InterruptedException | ExecutionException e) {
				logger.error("ERC20TOKEN transaction failed: {}", e.getMessage());
				e.printStackTrace();
				return new AsyncResult<Boolean>(false);
			}
		default:
			break;
		}
		return new AsyncResult<Boolean>(false);
	}

	/**
	 *  
	 */
	@Override
	public Page<Transaction> getListOfUserTransaction(User user, TransactionStatus transactionStatus, int pageNumber,
			int pageSize, String sortOrder, String sortBy) {

		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		if (TransactionStatus.WITHDRAW.equals(transactionStatus)) {
			return transactionRepo.findByFromUserAndTransactionStatus(user, transactionStatus, pageRequest);
		} else {
			return transactionRepo.findByToUserAndTransactionStatusOrTransactionStatus(user, pageRequest);
		}

	}

}
