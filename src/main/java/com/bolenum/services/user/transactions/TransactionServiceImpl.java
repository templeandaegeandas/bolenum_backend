/**
 * 
 */
package com.bolenum.services.user.transactions;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.bolenum.constant.UrlConstant;
import com.bolenum.enums.OrderType;
import com.bolenum.enums.TransactionStatus;
import com.bolenum.enums.TransactionType;
import com.bolenum.enums.TransferStatus;
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.Error;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.coin.Erc20Token;
import com.bolenum.model.coin.UserCoin;
import com.bolenum.model.fees.WithdrawalFee;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.common.coin.UserCoinRepository;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.admin.fees.WithdrawalFeeService;
import com.bolenum.services.common.coin.Erc20TokenService;
import com.bolenum.services.order.book.OrderAsyncService;
import com.bolenum.services.user.ErrorService;
import com.bolenum.services.user.UserService;
import com.bolenum.services.user.notification.NotificationService;
import com.bolenum.services.user.trade.TradeTransactionService;
import com.bolenum.services.user.wallet.WalletService;
import com.bolenum.util.CryptoUtil;
import com.bolenum.util.EthereumServiceUtil;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResourceUtils;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 * @modified Vishal Kumar
 * 
 * @change1: Buyer and Seller will pay the fee 0.15% on trading volume from the
 *           its own account, receiver will get full amount,For example Seller
 *           Have placed the Order, 1 ETH on 1 BTC price then Seller have to pay
 *           1 ETH and 0.15% fee that is 0.15 ETH as fee. So total ETH = 1 +
 *           0.15 = 1.15 ETH. Buyer will get 1 ETH
 * 
 *           BUYER has placed an order, 1 ETH on 1 BTC price then Buyer have to
 *           pay 1 BTC + 0.15 BTC(fee) = 1.15 BTC, Seller will get 1 BTC
 */
@Service
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
	private ErrorService errorService;

	@Autowired
	private Erc20TokenService erc20TokenService;

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private WithdrawalFeeService withdrawalFeeService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderAsyncService orderAsyncServices;

	@Autowired
	private WalletService walletService;

	@Autowired
	private UserCoinRepository userCoinRepository;

	@Value("${bitcoin.service.url}")
	private String btcUrl;

	@Value("${admin.email}")
	private String adminEmail;

	@Autowired
	private TradeTransactionService tradeTransactionService;

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
	public Future<Boolean> performEthTransaction(User fromUser, String tokenName, String toAddress, Double amount,
			TransactionStatus transactionStatus, Double fee, Long tradeId) {
		logger.debug("performing eth transaction: {} to address: {}, amount: {}", fromUser.getEmailId(), toAddress,
				GenericUtils.getDecimalFormatString(amount));

		User admin = userRepository.findByEmailId(adminEmail);

		// admin user coin
		UserCoin adminUserCoin = userCoinRepository.findByTokenNameAndUser(tokenName, admin);

		UserCoin fromUserCoin = userCoinRepository.findByTokenNameAndUser(tokenName, fromUser);
		String passwordKey = adminUserCoin.getWalletPwdKey();
		logger.debug("password key: {}", passwordKey);

		String fileName = ethWalletLocation + adminUserCoin.getWalletJsonFile();
		logger.debug("user eth wallet file name: {}", fileName);
		File walletFile = new File(fileName);
		try {
			String decrPwd = CryptoUtil.decrypt(adminUserCoin.getWalletPwd(), passwordKey);
			Credentials credentials = WalletUtils.loadCredentials(decrPwd, walletFile);

			EthSendTransaction ethSendTransaction = null;

			ethSendTransaction = transferEth(credentials, adminUserCoin.getWalletAddress(), amount);

			logger.debug("ETH transaction send completed: {}", ethSendTransaction.getTransactionHash());

			logger.debug("ETH transaction send fund completed");
			String txHash = ethSendTransaction.getTransactionHash();
			logger.debug("eth transaction hash:{} of user: {}, amount: {}", txHash, fromUser.getEmailId(), amount);
			Transaction transaction = transactionRepo.findByTxHash(txHash);
			logger.debug("transaction by hash: {}", transaction);

			if (transaction == null) {
				logger.debug("saving transaction for user: {}, hash: {}", fromUser.getEmailId(), txHash);
				transaction = new Transaction();
				transaction.setTxHash(transaction.getTxHash());
				transaction.setFromAddress(fromUserCoin.getWalletAddress());
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				transaction.setTransactionStatus(transactionStatus);
				transaction.setFromUser(fromUser);
				transaction.setCurrencyName(tokenName);

				if (fee != null) {
					transaction.setFee(fee);
				}

				UserCoin receiverUserCoin = userCoinRepository.findByWalletAddress(toAddress);

				User receiverUser = receiverUserCoin.getUser();

				if (receiverUser != null) {
					transaction.setToUser(receiverUser);
				}

				transaction.setTradeId(tradeId);

				Transaction saved = transactionRepo.saveAndFlush(transaction);
				logger.debug("transaction saved completed: {}", fromUser.getEmailId());
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(
							UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + fromUser.getUserId(),
							com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("message sent to websocket: {}", com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());

					return new AsyncResult<>(true);
				}
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | IOException | CipherException e) {
			logger.error("{} transaction failed:  {}", tokenName, e);
		}
		return new AsyncResult<>(false);
	}

	/**
	 * 
	 * @param credentials
	 * @param toAddress
	 * @param amount
	 * @return
	 */
	private EthSendTransaction transferEth(Credentials credentials, String toAddress, Double amount) {
		logger.debug("ETH transaction count started");
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		// get the next available nonce
		try {
			EthGetTransactionCount ethGetTransactionCount = web3j
					.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING).send();
			BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			logger.debug("ETH transaction count:{}", nonce);
			BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
			logger.debug("ETH transaction gas Price: {}", gasPrice);
			// create our transaction
			BigDecimal weiValue = Convert.toWei(String.valueOf(amount), Convert.Unit.ETHER);
			logger.debug("weiValue transaction: {}", weiValue);
			RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, Transfer.GAS_LIMIT,
					toAddress, weiValue.toBigIntegerExact());
			logger.debug("ETH raw transaction created");
			// sign & send our transaction
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			logger.debug("ETH raw transaction message signed");
			String hexValue = Numeric.toHexString(signedMessage);
			logger.debug("ETH transaction hex Value calculated and send started");
			return web3j.ethSendRawTransaction(hexValue).send();
		} catch (IOException e) {
			logger.error("ethereum transaction failed: {}", e);
			return null;
		}
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
	@Deprecated
	public Future<Boolean> performBtcTransaction(User fromUser, String toAddress, Double amount,
			TransactionStatus transactionStatus, Double feeE, Long tradeId) {
		logger.debug("performing btc tx : {} to address: {}, amount:{}", fromUser.getEmailId(), toAddress,
				GenericUtils.getDecimalFormatString(amount));
		Currency currency = currencyService.findByCurrencyAbbreviation("BTC");
		WithdrawalFee fee = null;
		double txFeePerKb = 0.001;
		if (currency != null) {
			fee = withdrawalFeeService.getWithdrawalFee(currency.getCurrencyId());
		}
		if (fee != null) {
			txFeePerKb = fee.getFee();
		}
		logger.debug("perform btc trnsaction with fee/KB: {}", GenericUtils.getDecimalFormatString(txFeePerKb));
		RestTemplate restTemplate = new RestTemplate();
		String url = btcUrl + UrlConstant.CREATE_TX;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject request = new JSONObject();
		try {
			request.put("walletId", fromUser.getBtcWalletUuid());
			request.put("transactionTradeAmount", String.valueOf(GenericUtils.getDecimalFormat().format(amount)));
			request.put("receiverAddress", toAddress);
			request.put("transactionFee", txFeePerKb);
		} catch (JSONException e) {
			logger.error("json parse error: {}", e);
		}
		HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
		try {
			ResponseEntity<String> txRes = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			if (txRes.getStatusCode() == HttpStatus.OK) {
				JSONObject responseJson = new JSONObject(txRes.getBody());
				logger.debug("json object of response: {}", responseJson);
				JSONObject data = (JSONObject) responseJson.get("data");
				String txHash = (String) data.get("transactionHash");
				logger.debug("transaction hash: {}", txHash);
				String txFee = String.valueOf(data.get("transactionFee"));
				logger.debug("transaction fee: {}", GenericUtils.getDecimalFormatString(Double.valueOf(txFee)));
				Transaction transaction = transactionRepo.findByTxHash(txHash);
				if (transaction == null) {
					transaction = new Transaction();
					transaction.setTxFee((txFee != null) ? Double.parseDouble(txFee) : 0);
					transaction.setTxHash(txHash);
					transaction.setFromAddress(fromUser.getBtcWalletAddress());
					transaction.setToAddress(toAddress);
					transaction.setTxAmount(amount);
					transaction.setTransactionType(TransactionType.OUTGOING);
					transaction.setFromUser(fromUser);
					transaction.setTransactionStatus(transactionStatus);
					transaction.setCurrencyName("BTC");
					if (feeE != null) {
						transaction.setFee(feeE);
					}
					User receiverUser = userRepository.findByBtcWalletAddress(toAddress);
					if (receiverUser != null) {
						transaction.setToUser(receiverUser);
						logger.debug("receiver user email id: {}", receiverUser.getEmailId());
					}
					transaction.setTradeId(tradeId);
					Transaction saved = transactionRepo.saveAndFlush(transaction);
					if (saved != null) {
						simpMessagingTemplate.convertAndSend(
								UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + fromUser.getUserId(),
								com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
						logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
						return new AsyncResult<>(true);
					}
				} else {
					logger.debug(" transaction exist hash: {}", transaction.getTxHash());
				}
			}
		} catch (JSONException e) {
			Error error = new Error(fromUser.getBtcWalletAddress(), toAddress,
					e.getMessage() + ", ERROR: transaction completed but transaction object not saved in db", "BTC",
					amount, false, tradeId);
			errorService.saveError(error);
			logger.debug("error saved: {}", error);
			logger.error("btc transaction exception:  {}", e);
		} catch (RestClientException e) {
			Error error = new Error(fromUser.getBtcWalletAddress(), toAddress, e.getMessage(), "BTC", amount, false,
					tradeId);
			errorService.saveError(error);
			logger.debug("error saved: {}", error);
			logger.error("btc transaction exception:  {}", e);
		}
		return new AsyncResult<>(false);
	}

	@Override
	@Async
	public Future<Boolean> performErc20Transaction(User fromUser, String tokenName, String toAddress, Double amount,
			TransactionStatus transactionStatus, Double fee, Long tradeId) {
		logger.debug("transaction started: {}", fromUser.getEmailId());
		UserCoin fromUserCoin;
		if (fromUser.getEmailId().equals(adminEmail)) {
			fromUserCoin = userCoinRepository.findByTokenNameAndUser("ETH", fromUser);
		} else {
			fromUserCoin = userCoinRepository.findByTokenNameAndUser(tokenName, fromUser);
		}
		try {
			Erc20Token erc20Token = erc20TokenService.getByCoin(tokenName);
			User admin = userRepository.findByEmailId(adminEmail);
			TransactionReceipt transactionReceipt = erc20TokenService.transferErc20Token(admin, erc20Token, toAddress,
					amount, "ETH");
			logger.debug("{} transaction send fund completed", tokenName);
			String txHash = transactionReceipt.getTransactionHash();
			logger.debug("{} transaction hash: {} of user: {}, amount: {}", tokenName, txHash, fromUser.getEmailId(),
					amount);
			Transaction transaction = transactionRepo.findByTxHash(txHash);
			logger.debug("transaction by hash: {}", transaction);
			if (transaction == null) {
				logger.debug("saving transaction for user: {}, hash: {}", fromUser.getEmailId(), txHash);
				transaction = new Transaction();
				transaction.setTxHash(transactionReceipt.getTransactionHash());
				transaction.setFromAddress(fromUserCoin.getWalletAddress());
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				transaction.setTransactionStatus(transactionStatus);
				transaction.setFromUser(fromUser);
				transaction.setCurrencyName(tokenName);
				if (fee != null) {
					transaction.setFee(fee);
				}
				UserCoin receiverUserCoin = userCoinRepository.findByWalletAddress(toAddress);
				if (receiverUserCoin != null) {
					transaction.setToUser(receiverUserCoin.getUser());

				}
				transaction.setTradeId(tradeId);
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				logger.debug("transaction saved completed: {}", fromUser.getEmailId());
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(
							UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + fromUser.getUserId(),
							com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("message sent to websocket: {}", com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
					return new AsyncResult<>(true);
				}
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | IOException | CipherException | TransactionException | InterruptedException
				| ExecutionException e) {
			logger.error("{} transaction failed:  {}", tokenName, e);
		}
		return new AsyncResult<>(false);
	}

	/**
	 * 
	 */
	@Override
	@Async
	@Deprecated
	public Future<Boolean> performTransaction(String currencyAbr, double qtyTraded, User buyer, User seller,
			boolean isFee, Long tradeId) {

		String currencyType = currencyService.findByCurrencyAbbreviation(currencyAbr).getCurrencyType().toString();
		String msg = "";
		String msg1 = "";
		logger.debug("perform transaction for admin fee: {}", isFee);
		if (!isFee) {
			msg = "Hi " + seller.getFirstName() + ", Your transaction of selling "
					+ GenericUtils.getDecimalFormatString(qtyTraded) + " " + currencyAbr
					+ " have been processed successfully!";
			msg1 = "Hi " + buyer.getFirstName() + ", Your transaction of buying "
					+ GenericUtils.getDecimalFormatString(qtyTraded) + " " + currencyAbr
					+ " have been processed successfully!";
		}
		Future<Boolean> txStatus;
		switch (currencyType) {
		case "CRYPTO":
			switch (currencyAbr) {
			case "BTC":
				logger.debug("BTC transaction started");
				txStatus = performBtcTransaction(seller, buyer.getBtcWalletAddress(), qtyTraded, null, null, tradeId);
				try {
					boolean res = txStatus.get();
					logger.debug("is BTC transaction successed: {}", res);
					/**
					 * if transaction for users, then return result with mail notification to users
					 */
					if (res && !isFee) {
						notificationService.sendNotification(seller, msg);
						notificationService.saveNotification(buyer, seller, msg);
						notificationService.sendNotification(buyer, msg1);
						notificationService.saveNotification(buyer, seller, msg1);
						logger.debug("Message : {}", msg);
						logger.debug("Message : {}", msg1);
						return new AsyncResult<>(res);
					}
					/**
					 * if transaction for admin, then return result without mail notification
					 */
					if (res && isFee) {
						return new AsyncResult<>(res);
					}
				} catch (InterruptedException | ExecutionException e) {
					logger.error("BTC transaction failed: {}", e);
					return new AsyncResult<>(false);
				}
				break;
			case "ETH":
				logger.debug("ETH transaction started");
				txStatus = performEthTransaction(seller, "ETH", buyer.getEthWalletaddress(), qtyTraded, null, null,
						tradeId);
				try {
					boolean res = txStatus.get();
					logger.debug("is ETH transaction successed: {}", res);
					/**
					 * if transaction for users, then return result with mail notification to users
					 */
					if (res && !isFee) {
						notificationService.sendNotification(seller, msg);
						notificationService.saveNotification(buyer, seller, msg);
						notificationService.sendNotification(buyer, msg1);
						notificationService.saveNotification(buyer, seller, msg1);
						logger.debug("Message : {}", msg);
						logger.debug("Message : {}", msg1);
						return new AsyncResult<>(res);
					}
					/**
					 * if transaction for admin, then return result without mail notification
					 */
					if (res && isFee) {
						return new AsyncResult<>(res);
					}
				} catch (InterruptedException | ExecutionException e) {
					logger.error("ETH transaction failed: {}", e);
					return new AsyncResult<>(false);
				}
			}
			break;

		case "ERC20TOKEN":
			logger.debug("ERC20TOKEN transaction started");
			boolean res = tradeTransactionService.performErc20Trade(seller, currencyAbr, buyer, qtyTraded, tradeId);

			logger.debug("is ERC20TOKEN transaction successed: {}", res);
			/**
			 * if transaction for users, then return result with mail notification to users
			 */
			if (res && !isFee) {
				notificationService.sendNotification(seller, msg);
				notificationService.saveNotification(buyer, seller, msg);
				notificationService.sendNotification(buyer, msg1);
				notificationService.saveNotification(buyer, seller, msg1);
				logger.debug("Message : {}", msg);
				logger.debug("Message : {}", msg1);
				return new AsyncResult<>(res);
			}
			/**
			 * if transaction for admin, then return result without mail notification
			 */
			if (res && isFee) {
				return new AsyncResult<>(res);
			}

			break;
		default:
			break;
		}
		return new AsyncResult<>(false);
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
			Page<Transaction> list = transactionRepo.findByFromUserAndTransactionStatus(user, transactionStatus,
					pageRequest);
			fetchTransactionConfirmation(list);
			fetchBTCConfirmation(list);
			return list;
		} else {
			Page<Transaction> list = transactionRepo.findByToUserAndTransactionStatusOrTransactionStatus(user,
					pageRequest);
			fetchTransactionConfirmation(list);
			fetchBTCConfirmation(list);
			return list;
		}
	}

	@Override
	@Async
	public Future<Boolean> processTransaction(Orders matchedOrder, Orders orders, double qtyTraded, User buyer,
			User seller, double remainingVolume, double buyerTradeFee, double sellerTradeFee, Trade trade) {
		logger.debug("thread: {} going to sleep for 10 Secs ", Thread.currentThread().getName());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			logger.error("exception: {}", e);
		}
		logger.debug("thread: {} waked up after for 10 Secs ", Thread.currentThread().getName());
		String msg = "";
		String msg1 = "";
		logger.debug("buyer: {} and seller: {} for order: {}", buyer.getEmailId(), seller.getEmailId(),
				matchedOrder.getId());
		CurrencyPair currencyPair = matchedOrder.getPair();

		String toCurrAbrrivaiton = currencyPair.getPairedCurrency().get(0).getCurrencyAbbreviation();
		String pairCurrAbrrivaiton = currencyPair.getToCurrency().get(0).getCurrencyAbbreviation();
		String toCurrencyType = currencyPair.getPairedCurrency().get(0).getCurrencyType().toString();
		String pairCurrencyType = currencyPair.getToCurrency().get(0).getCurrencyType().toString();
		String qtr = walletService.getPairedBalance(matchedOrder, currencyPair, qtyTraded);
		logger.debug("paired currency volume: {} {}", GenericUtils.getDecimalFormatString(Double.valueOf(qtr)),
				pairCurrAbrrivaiton);
		if (OrderType.BUY.equals(orders.getOrderType())) {
			logger.debug("BUY Order");

			msg = "Hi " + buyer.getFirstName() + ", Your " + orders.getOrderType()
					+ " order has been initiated, quantity: " + GenericUtils.getDecimalFormatString(qtyTraded) + " "
					+ toCurrAbrrivaiton + ", on " + GenericUtils.getDecimalFormatString(Double.valueOf(qtr)) + " "
					+ pairCurrAbrrivaiton + " remaining voloume: "
					+ GenericUtils.getDecimalFormatString(remainingVolume) + " " + toCurrAbrrivaiton;
			logger.debug("Byuer's transaction initiated msg: {}", msg);

			msg1 = "Hi " + seller.getFirstName() + ", Your " + matchedOrder.getOrderType()
					+ " order has been initiated, quantity: " + GenericUtils.getDecimalFormatString(qtyTraded) + " "
					+ toCurrAbrrivaiton + ", on " + GenericUtils.getDecimalFormatString(Double.valueOf(qtr)) + " "
					+ pairCurrAbrrivaiton + " remaining voloume: "
					+ GenericUtils.getDecimalFormatString(matchedOrder.getVolume()) + " " + toCurrAbrrivaiton;

			logger.debug("Seller's transaction initiated msg: {}", msg1);
		} else {
			logger.debug("SELL Order");
			msg1 = "Hi " + seller.getFirstName() + ", Your " + orders.getOrderType()
					+ " order has been initiated, quantity: " + GenericUtils.getDecimalFormatString(qtyTraded) + " "
					+ toCurrAbrrivaiton + ", on " + GenericUtils.getDecimalFormatString(Double.valueOf(qtr)) + " "
					+ pairCurrAbrrivaiton + " remaining voloume: "
					+ GenericUtils.getDecimalFormatString(remainingVolume) + " " + toCurrAbrrivaiton;
			logger.debug("Seller's msg1: {}", msg1);
			msg = "Hi " + buyer.getFirstName() + ", Your " + matchedOrder.getOrderType()
					+ " order has been initiated, quantity: " + GenericUtils.getDecimalFormatString(qtyTraded) + " "
					+ toCurrAbrrivaiton + ", on " + GenericUtils.getDecimalFormatString(Double.valueOf(qtr)) + " "
					+ pairCurrAbrrivaiton + " remaining voloume: "
					+ GenericUtils.getDecimalFormatString(matchedOrder.getVolume()) + " " + toCurrAbrrivaiton;
			logger.debug("Byuer's msg: {}", msg);
		}

		if (qtr != null && Double.valueOf(qtr) > 0) {
			logger.debug("actual quantity buyer: {}, will get: {} {}", buyer.getFirstName(),
					GenericUtils.getDecimalFormatString(qtyTraded), toCurrAbrrivaiton);
			/**
			 * Seller performing transaction; to send ETH to buyer in case of ETH/BTC pair
			 */
			boolean res = tradeTransactionService.performTradeTransaction(toCurrAbrrivaiton, toCurrencyType, qtyTraded,
					buyer, seller, trade.getId());

			logger.debug("Seller performed trade transaction: {}", res);
			if (res) {
				logger.debug("Seller: {} has performed tx to buyer:{} of amount: {} {}", seller.getEmailId(),
						buyer.getEmailId(), GenericUtils.getDecimalFormatString(qtyTraded), toCurrAbrrivaiton);
				trade.setIsTxSeller(true);
				trade = orderAsyncServices.saveTrade(trade);
				logger.debug("seller tx perfrom status saved: {}", trade.getIsTxSeller());
				/**
				 * unlocking locked volume
				 */
				logger.debug("seller locked volume, unlocking started");
				if (OrderType.BUY.equals(orders.getOrderType())) {
					double lockedVolRemaining = matchedOrder.getLockedVolume() - qtyTraded;
					logger.debug("seller unlock volume: {}, remaining locked volume: {} ",
							GenericUtils.getDecimalFormatString(qtyTraded),
							GenericUtils.getDecimalFormatString(lockedVolRemaining));
					if (lockedVolRemaining < 0) {
						lockedVolRemaining = 0;
					}
					matchedOrder.setLockedVolume(lockedVolRemaining);
					orderAsyncServices.saveOrder(matchedOrder);
					logger.debug("seller locked volume, unlocking completed amount: {}", qtyTraded);
				} else {
					double lockedVolRemaining = orders.getLockedVolume() - qtyTraded;
					logger.debug("seller unlock volume: {}, remaining locked volume: {} ",
							GenericUtils.getDecimalFormatString(qtyTraded),
							GenericUtils.getDecimalFormatString(lockedVolRemaining));
					if (lockedVolRemaining < 0) {
						lockedVolRemaining = 0;
					}
					orders.setLockedVolume(lockedVolRemaining);
					orderAsyncServices.saveOrder(orders);
					logger.debug("seller locked volume, unlocking completed amount: {}",
							GenericUtils.getDecimalFormatString(qtyTraded));
				}

			}
			double sellerQty = GenericUtils.getDecimalFormat(Double.valueOf(qtr) - sellerTradeFee);
			logger.debug("actual quantity seller will get: {} {}", GenericUtils.getDecimalFormatString(sellerQty),
					pairCurrAbrrivaiton);
			/**
			 * Buyer performing transaction; to send BTC to Seller in case of ETH/BTC pair
			 */
			boolean buyerRes = tradeTransactionService.performTradeTransaction(pairCurrAbrrivaiton, pairCurrencyType,
					sellerQty, seller, buyer, trade.getId());
			logger.debug("buyer performed trade transaction: {}", buyerRes);
			if (buyerRes) {
				logger.debug("Buyer: {} has performed tx to Seller:{} of amount: {} {}", buyer.getEmailId(),
						seller.getEmailId(), GenericUtils.getDecimalFormatString(sellerQty), pairCurrAbrrivaiton);
				trade.setIsTxBuyer(true);
				trade = orderAsyncServices.saveTrade(trade);
				logger.debug("Buyer tx perfrom status saved: {}", trade.getIsTxBuyer());
				/**
				 * unlocking locked volume
				 */
				logger.debug("buyer locked volume, unlocking started");
				if (OrderType.BUY.equals(orders.getOrderType())) {
					double uvol = matchedOrder.getPrice() * qtyTraded - buyerTradeFee;
					double lockedVolRemaining = orders.getLockedVolume() - uvol;
					logger.debug("buyer unlock volume: {}, remaining locked volume: {} ",
							GenericUtils.getDecimalFormatString(uvol),
							GenericUtils.getDecimalFormatString(lockedVolRemaining));
					if (lockedVolRemaining < 0) {
						lockedVolRemaining = 0;
					}
					orders.setLockedVolume(lockedVolRemaining);
					orderAsyncServices.saveOrder(orders);
					logger.debug("buyer locked volume, unlocking completed for amount: {}",
							GenericUtils.getDecimalFormatString(uvol));
				} else {
					double uvol = matchedOrder.getPrice() * qtyTraded - buyerTradeFee;
					double lockedVolRemaining = matchedOrder.getLockedVolume() - uvol;
					logger.debug("buyer unlock volume: {}, remaining locked volume: {} ",
							GenericUtils.getDecimalFormatString(uvol),
							GenericUtils.getDecimalFormatString(lockedVolRemaining));
					if (lockedVolRemaining < 0) {
						lockedVolRemaining = 0;
					}
					matchedOrder.setLockedVolume(lockedVolRemaining);
					orderAsyncServices.saveOrder(matchedOrder);
					logger.debug("buyer locked volume, unlocking completed amount: {}",
							GenericUtils.getDecimalFormatString(uvol));
				}
			}
			User admin = userService.findByEmail(adminEmail);
			double tfee = GenericUtils.getDecimalFormat(buyerTradeFee + sellerTradeFee);
			logger.debug(
					"actual quantity admin will get from buyer: {} and seller: {} total fee: {} {} of trade Id: {} ",
					GenericUtils.getDecimalFormatString(buyerTradeFee),
					GenericUtils.getDecimalFormatString(sellerTradeFee), GenericUtils.getDecimalFormatString(tfee),
					pairCurrAbrrivaiton, trade.getId());
			boolean feeRes = tradeTransactionService.performTradeTransactionFee(pairCurrAbrrivaiton, pairCurrencyType,
					tfee, admin, buyer, trade.getId());
			logger.debug("trade fee transaction: {}", feeRes);
			if (feeRes) {
				trade.setIsFeeDeductedBuyer(true);
				trade.setIsFeeDeductedSeller(true);
				if (trade.getIsTxBuyer() && trade.getIsTxSeller()) {
					trade.setStatus(true);
				}
				logger.debug("Set buyer trade fee is deducted: {}", trade.getIsFeeDeductedBuyer());
				logger.debug("Saving trade fee for buyer started");
				trade = orderAsyncServices.saveTrade(trade);
				logger.debug("Saving trade fee for buyer completed: {}", trade.getIsFeeDeductedBuyer());

				logger.debug("buyer fee locked volume, unlocking started");
				if (OrderType.BUY.equals(orders.getOrderType())) {
					double uvol = buyerTradeFee * 2;
					double lockedVolRemaining = orders.getLockedVolume() - uvol;
					logger.debug("buyer fee unlock volume: {}, remaining locked volume: {} ",
							GenericUtils.getDecimalFormatString(uvol),
							GenericUtils.getDecimalFormatString(lockedVolRemaining));
					if (lockedVolRemaining < 0) {
						lockedVolRemaining = 0;
					}
					orders.setLockedVolume(lockedVolRemaining);
					orderAsyncServices.saveOrder(orders);
					logger.debug("buyer fee locked volume, unlocking completed for amount: {}",
							GenericUtils.getDecimalFormatString(buyerTradeFee));
				} else {
					double uvol = buyerTradeFee * 2;
					double lockedVolRemaining = matchedOrder.getLockedVolume() - uvol;
					logger.debug("buyer fee unlock volume: {}, remaining locked volume: {} ",
							GenericUtils.getDecimalFormatString(uvol),
							GenericUtils.getDecimalFormatString(lockedVolRemaining));
					if (lockedVolRemaining < 0) {
						lockedVolRemaining = 0;
					}
					matchedOrder.setLockedVolume(lockedVolRemaining);
					orderAsyncServices.saveOrder(matchedOrder);
					logger.debug("buyer locked volume, unlocking completed amount: {}",
							GenericUtils.getDecimalFormatString(uvol));
				}
			}
		} else {
			logger.debug("transaction processing failed due to paired currency volume");
			return new AsyncResult<>(false);
		}
		return new AsyncResult<>(true);
	}

	/**
	 * 
	 */
	@Override
	public void fetchTransactionConfirmation(Page<Transaction> page) {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		List<Transaction> list = new ArrayList<>();
		String status = "CONFIRMED";
		page.forEach(transaction -> {
			try {
				if (!"BTC".equalsIgnoreCase(transaction.getCurrencyName()) && transaction.getTxHash() != null
						&& !status.equals(transaction.getTxStatus()) && !transaction.isInAppTransaction()) {
					TransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transaction.getTxHash())
							.send().getResult();
					if (transactionReceipt != null) {
						BigInteger txBlockNumber = transactionReceipt.getBlockNumber();
						BigInteger ethBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
						BigInteger blockNumber = ethBlockNumber.subtract(txBlockNumber);
						logger.debug("number: {}", blockNumber);
						if (blockNumber.compareTo(BigInteger.valueOf(12)) > 0) {
							transaction.setNoOfConfirmations(12);
							transaction.setTxStatus(status);
						} else {
							transaction.setNoOfConfirmations(blockNumber.intValue());
						}
						list.add(transaction);
					}
				}
			} catch (IOException e) {
				logger.error("fetch tx confirmation error: {}", e);
			}
		});
		transactionRepo.save(list);
	}

	/**
	 * 
	 * @param page
	 */

	public void fetchBTCConfirmation(Page<Transaction> page) {
		String status = "CONFIRMED";
		page.forEach(transaction -> {
			if ("BTC".equalsIgnoreCase(transaction.getCurrencyName()) && transaction.getTxHash() != null
					&& !status.equals(transaction.getTxStatus()) && !transaction.isInAppTransaction()) {

				try {
					BtcdClient btcdClient = ResourceUtils.getBtcdProvider();
					com.neemre.btcdcli4j.core.domain.Transaction transaction1 = btcdClient
							.getTransaction(transaction.getTxHash());
					if (transaction1.getConfirmations() >= 6) {
						transaction.setNoOfConfirmations(6);
						transaction.setTxStatus(status);
						transactionRepo.save(transaction);
					} else {
						transaction.setNoOfConfirmations(transaction1.getConfirmations());
						transactionRepo.save(transaction);
					}
				} catch (BitcoindException | CommunicationException e) {
					logger.error("btc transaction confiramtion fetch error: {}", e);
				}
			}
		});

	}

	/**
	 *    
	 */
	@Override
	public boolean withdrawErc20Token(User fromUser, String tokenName, String toAddress, Double amount,
			TransactionStatus transactionStatus, Double fee, Long tradeId) {
		UserCoin senderUserCoin = userCoinRepository.findByTokenNameAndUser(tokenName, fromUser);
		if (senderUserCoin != null) {
			senderUserCoin.setBalance(senderUserCoin.getBalance() - amount);
			userCoinRepository.save(senderUserCoin);
			UserCoin receiverUserCoin = userCoinRepository.findByWalletAddress(toAddress);
			if (receiverUserCoin != null) {
				receiverUserCoin.setBalance(receiverUserCoin.getBalance() + (amount - fee));
				userCoinRepository.save(receiverUserCoin);
				return saveInAppTransaction(fromUser, senderUserCoin, receiverUserCoin, toAddress, tokenName,
						(amount - fee), fee);
			} else {
				performErc20Transaction(fromUser, tokenName, toAddress, amount - fee, TransactionStatus.WITHDRAW, fee,
						null);
				return true;
			}
		}
		return false;
	}

	/**
	 * @created by Himanshu Kumar to withdraw ETH
	 */
	@Override
	public boolean withdrawETH(User fromUser, String tokenName, String toAddress, Double amount,
			TransactionStatus transactionStatus, Double fee, Long tradeId) {
		UserCoin senderUserCoin = userCoinRepository.findByTokenNameAndUser(tokenName, fromUser);
		if (senderUserCoin != null) {
			senderUserCoin.setBalance(senderUserCoin.getBalance() - amount);
			userCoinRepository.save(senderUserCoin);
			UserCoin receiverUserCoin = userCoinRepository.findByWalletAddress(toAddress);
			if (receiverUserCoin != null) {
				receiverUserCoin.setBalance(receiverUserCoin.getBalance() + (amount - fee));
				userCoinRepository.save(receiverUserCoin);
				return saveInAppTransaction(fromUser, senderUserCoin, receiverUserCoin, toAddress, tokenName,
						(amount - fee), fee);
			} else {
				performEthTransaction(fromUser, tokenName, toAddress, amount - fee, TransactionStatus.WITHDRAW, fee,
						null);
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean withdrawBTC(User fromUser, String tokenName, String toAddress, Double amount, Double fee) {
		UserCoin senderUserCoin = userCoinRepository.findByTokenNameAndUser(tokenName, fromUser);
		logger.debug("withdraw btc of user: {} amount: {}, to address: {}", fromUser.getEmailId(), amount, toAddress);
		if (senderUserCoin != null) {
			UserCoin receiverUserCoin = userCoinRepository.findByWalletAddress(toAddress);
			logger.debug("btc withdraw in app user: {}", receiverUserCoin);
			if (receiverUserCoin != null) {
				boolean result = tradeTransactionService.performBtcTrade(fromUser, receiverUserCoin.getUser(), amount,
						null);
				if (result) {
					return saveInAppTransaction(fromUser, senderUserCoin, receiverUserCoin, toAddress, tokenName,
							amount, fee);
				}
				return false;
			} else {
				return performBtcTransaction(fromUser, toAddress, amount, fee);
			}
		}
		logger.error("btc withdraw user not exist: {}", senderUserCoin);
		return false;
	}

	private boolean saveInAppTransaction(User fromUser, UserCoin senderUserCoin, UserCoin receiverUserCoin,
			String toAddress, String tokenName, Double amount, Double fee) {
		Transaction transaction = new Transaction();
		String txHash = UUID.randomUUID().toString();
		transaction.setTxHash("InApp-" + txHash);
		transaction.setFromAddress(senderUserCoin.getWalletAddress());
		transaction.setToAddress(toAddress);
		transaction.setTxAmount(amount);
		transaction.setTransactionType(TransactionType.OUTGOING);
		transaction.setTransactionStatus(TransactionStatus.WITHDRAW);
		transaction.setFromUser(fromUser);
		transaction.setCurrencyName(tokenName);
		transaction.setFee(fee);
		transaction.setToUser(receiverUserCoin.getUser());
		transaction.setInAppTransaction(true);
		transaction.setTxStatus("CONFIRMED");
		Transaction saved = transactionRepo.saveAndFlush(transaction);
		logger.debug("transaction saved completed: {}", fromUser.getEmailId());
		if (saved != null) {
			simpMessagingTemplate.convertAndSend(
					UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + fromUser.getUserId(),
					com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
			logger.debug("message sent to websocket: {}", com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
			logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
			return true;
		}
		return false;
	}

	/**
	 * this will do real btc transaction from user account to btc address
	 */
	@Override
	public Boolean performBtcTransaction(User fromUser, String toAddress, Double amount, Double fee) {
		try {
			BtcdClient client = ResourceUtils.getBtcdProvider();
			BigDecimal currentBal = client.getBalance(String.valueOf(fromUser.getUserId()));
			BigDecimal balance = BigDecimal.valueOf(amount);
			logger.debug("user: {} has current account balance:{} and withdraw amount: {}", fromUser.getEmailId(),
					GenericUtils.getDecimalFormatString(currentBal.doubleValue()),
					GenericUtils.getDecimalFormatString(balance.doubleValue()));
			if (currentBal.compareTo(balance) < 0) {
				logger.error("User: {} has insufficent balance to withdraw amount: {}", fromUser.getEmailId(), balance);
				return false;
			}
			String txHash = client.sendFrom(String.valueOf(fromUser.getUserId()), toAddress,
					BigDecimal.valueOf(amount));
			if (txHash == null) {
				return false;
			}
			logger.debug("transaction hash: {} of btc tx of user: {} amount: {}", txHash, fromUser.getEmailId(),
					GenericUtils.getDecimalFormatString(balance.doubleValue()));
			Transaction transaction = transactionRepo.findByTxHash(txHash);
			if (transaction == null) {
				transaction = new Transaction();
				transaction.setTxHash(txHash);
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				transaction.setTransactionStatus(TransactionStatus.WITHDRAW);
				transaction.setTransferStatus(TransferStatus.COMPLETED);
				transaction.setFromUser(fromUser);
				transaction.setCurrencyName("BTC");
				transaction.setFee(fee);
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(
							UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + fromUser.getUserId(),
							com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
					return true;
				}
			} else {
				logger.debug(" transaction exist hash: {}", transaction.getTxHash());
			}
		} catch (BitcoindException | CommunicationException e) {
			logger.error("BTC account balance error: {}", e);
		}
		return false;
	}
}