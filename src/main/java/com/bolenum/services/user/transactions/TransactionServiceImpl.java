/**
 * 
 */
package com.bolenum.services.user.transactions;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
import org.springframework.transaction.annotation.Transactional;
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
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.Erc20Token;
import com.bolenum.model.Error;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.fees.WithdrawalFee;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.common.CurrencyPairRepo;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.admin.Erc20TokenService;
import com.bolenum.services.admin.fees.WithdrawalFeeService;
import com.bolenum.services.order.book.OrderAsyncService;
import com.bolenum.services.user.ErrorService;
import com.bolenum.services.user.UserService;
import com.bolenum.services.user.notification.NotificationService;
import com.bolenum.services.user.wallet.WalletService;
import com.bolenum.util.CryptoUtil;
import com.bolenum.util.EthereumServiceUtil;
import com.bolenum.util.GenericUtils;

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

	@Value("${bitcoin.service.url}")
	private String btcUrl;

	@Value("${admin.email}")
	private String adminEmail;

	@Autowired
	private CurrencyPairRepo currencyPairRepo;

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
			TransactionStatus transactionStatus, Double fee, Long tradeId) {
		logger.debug("performing eth transaction: {} to address: {}, amount: {}", fromUser.getEmailId(), toAddress,
				GenericUtils.getDecimalFormatString(amount));
		String passwordKey = fromUser.getEthWalletPwdKey();
		logger.debug("password key: {}", passwordKey);

		String fileName = ethWalletLocation + fromUser.getEthWalletJsonFileName();
		logger.debug("user eth wallet file name: {}", fileName);
		File walletFile = new File(fileName);
		try {
			String decrPwd = CryptoUtil.decrypt(fromUser.getEthWalletPwd(), passwordKey);
			// logger.debug("decr password: {}", decrPwd);
			EthSendTransaction ethSendTransaction = null;
			try {
				logger.debug("ETH transaction credentials load started");
				Credentials credentials = WalletUtils.loadCredentials(decrPwd, walletFile);
				logger.debug("ETH transaction credentials load completed");
				ethSendTransaction = transferEth(credentials, toAddress, amount);
				logger.debug("ETH transaction send completed: {}", ethSendTransaction.getTransactionHash());
			} catch (Exception e) {
				Error error = new Error(fromUser.getEthWalletaddress(), toAddress, e.getMessage(), "ETH", amount, false,
						tradeId);
				errorService.saveError(error);
				logger.debug("error saved: {}", error);
				return new AsyncResult<Boolean>(false);
			}
			logger.debug("ETH transaction send fund completed");
			String txHash = ethSendTransaction.getTransactionHash();
			logger.debug("eth transaction hash:{} of user: {}, amount: {}", txHash, fromUser.getEmailId(), amount);
			Transaction transaction = transactionRepo.findByTransactionHash(txHash);
			logger.debug("transaction by hash: {}", transaction);
			if (transaction == null) {
				transaction = new Transaction();
				transaction.setTxHash(ethSendTransaction.getTransactionHash());
				transaction.setFromAddress(fromUser.getEthWalletaddress());
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				transaction.setTransactionStatus(transactionStatus);
				transaction.setFromUser(fromUser);
				transaction.setCurrencyName("ETH");
				if (fee != null) {
					transaction.setFee(fee);
				}
				User receiverUser = userRepository.findByEthWalletaddress(toAddress);
				if (receiverUser != null) {
					transaction.setToUser(receiverUser);
				}
				transaction.setTradeId(tradeId);
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(
							UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + fromUser.getUserId(),
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
			logger.error("ethereum transaction failed: {}", e.getMessage());
			e.printStackTrace();
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
		logger.debug("perform btc trnsaction with fee/KB: {}", GenericUtils.getDecimalFormat().format(txFeePerKb));
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
				String txFee = String.valueOf(data.get("transactionFee"));
				logger.debug("transaction fee: {}", GenericUtils.getDecimalFormatString(Double.valueOf(txFee)));
				Transaction transaction = transactionRepo.findByTransactionHash(txHash);
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
						return new AsyncResult<Boolean>(true);
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
			logger.error("btc transaction exception:  {}", e.getMessage());
			e.printStackTrace();
		} catch (RestClientException e) {
			Error error = new Error(fromUser.getBtcWalletAddress(), toAddress, e.getMessage(), "BTC", amount, false,
					tradeId);
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
			TransactionStatus transactionStatus, Double fee, Long tradeId) {
		try {
			Erc20Token erc20Token = erc20TokenService.getByCoin(tokenName);
			TransactionReceipt transactionReceipt = erc20TokenService.transferErc20Token(fromUser, erc20Token,
					toAddress, amount);
			logger.debug("{} transaction send fund completed", tokenName);
			String txHash = transactionReceipt.getTransactionHash();
			logger.debug("{} transaction hash: {} of user: {}, amount: {}", tokenName, txHash, fromUser.getEmailId(),
					amount);
			Thread.sleep(500);
			Transaction transaction = transactionRepo.findByTransactionHash(txHash);
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
				if (fee != null) {
					transaction.setFee(fee);
				}
				User receiverUser = userRepository.findByEthWalletaddress(toAddress);
				if (receiverUser != null) {
					transaction.setToUser(receiverUser);

				}
				transaction.setTradeId(tradeId);
				Transaction saved = null;
				if (transactionRepo.findByTransactionHash(txHash) == null) {
					try {
						saved = transactionRepo.save(transaction);
					} catch (Exception e) {
						logger.debug("exception in saving: {}", e.getMessage());
					}
				}
				logger.debug("transaction saved completed: {}", fromUser.getEmailId());
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(
							UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + fromUser.getUserId(),
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
				transaction.setTradeId(tradeId);
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				logger.debug("transaction else part saved completed: {}", fromUser.getEmailId());
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(
							UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + fromUser.getUserId(),
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
	public Future<Boolean> performTransaction(String currencyAbr, double qtyTraded, User buyer, User seller,
			boolean isFee, Long tradeId) {

		String currencyType = currencyService.findByCurrencyAbbreviation(currencyAbr).getCurrencyType().toString();
		String msg = "", msg1 = "";
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
					 * if transaction for users, then return result with mail
					 * notification to users
					 */
					if (res && !isFee) {
						notificationService.sendNotification(seller, msg);
						notificationService.saveNotification(buyer, seller, msg);
						notificationService.sendNotification(buyer, msg1);
						notificationService.saveNotification(buyer, seller, msg1);
						logger.debug("Message : {}", msg);
						logger.debug("Message : {}", msg1);
						return new AsyncResult<Boolean>(res);
					}
					/**
					 * if transaction for admin, then return result without mail
					 * notification
					 */
					if (res && isFee) {
						return new AsyncResult<Boolean>(res);
					}
				} catch (InterruptedException | ExecutionException e) {
					logger.error("BTC transaction failed: {}", e.getMessage());
					e.printStackTrace();
					return new AsyncResult<Boolean>(false);
				}
			case "ETH":
				logger.debug("ETH transaction started");
				txStatus = performEthTransaction(seller, buyer.getEthWalletaddress(), qtyTraded, null, null, tradeId);
				try {
					boolean res = txStatus.get();
					logger.debug("is ETH transaction successed: {}", res);
					/**
					 * if transaction for users, then return result with mail
					 * notification to users
					 */
					if (res && !isFee) {
						notificationService.sendNotification(seller, msg);
						notificationService.saveNotification(buyer, seller, msg);
						notificationService.sendNotification(buyer, msg1);
						notificationService.saveNotification(buyer, seller, msg1);
						logger.debug("Message : {}", msg);
						logger.debug("Message : {}", msg1);
						return new AsyncResult<Boolean>(res);
					}
					/**
					 * if transaction for admin, then return result without mail
					 * notification
					 */
					if (res && isFee) {
						return new AsyncResult<Boolean>(res);
					}
				} catch (InterruptedException | ExecutionException e) {
					logger.error("ETH transaction failed: {}", e.getMessage());
					e.printStackTrace();
					return new AsyncResult<Boolean>(false);
				}
			}
			break;

		case "ERC20TOKEN":
			logger.debug("ERC20TOKEN transaction started");
			txStatus = performErc20Transaction(seller, currencyAbr, buyer.getEthWalletaddress(), qtyTraded, null, null,
					tradeId);
			try {
				boolean res = txStatus.get();
				logger.debug("is ERC20TOKEN transaction successed: {}", res);
				/**
				 * if transaction for users, then return result with mail
				 * notification to users
				 */
				if (res && !isFee) {
					notificationService.sendNotification(seller, msg);
					notificationService.saveNotification(buyer, seller, msg);
					notificationService.sendNotification(buyer, msg1);
					notificationService.saveNotification(buyer, seller, msg1);
					logger.debug("Message : {}", msg);
					logger.debug("Message : {}", msg1);
					return new AsyncResult<Boolean>(res);
				}
				/**
				 * if transaction for admin, then return result without mail
				 * notification
				 */
				if (res && isFee) {
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

	@Override
	@Async
	@Transactional
	public Future<Boolean> processTransaction(Orders matchedOrder, Orders orders, double qtyTraded, User buyer,
			User seller, double remainingVolume, double buyerTradeFee, double sellerTradeFee, Trade trade) {
		logger.debug("thread: {} going to sleep for 10 Secs ", Thread.currentThread().getName());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			logger.error("exception: {}", e.getMessage());
			e.printStackTrace();
		}
		logger.debug("thread: {} waked up after for 10 Secs ", Thread.currentThread().getName());
		logger.debug("buyer trade fee: {} seller trade fee: {}", GenericUtils.getDecimalFormatString(buyerTradeFee),
				GenericUtils.getDecimalFormatString(sellerTradeFee));
		String msg = "", msg1 = "";
		logger.debug("buyer: {} and seller: {} for order: {}", buyer.getEmailId(), seller.getEmailId(),
				matchedOrder.getId());
		logger.debug("pair id:{}", matchedOrder.getPair().getPairId());
		// finding currency pair
		CurrencyPair currencyPair = currencyPairRepo.findByPairId(matchedOrder.getPair().getPairId());// currencyPairService.findCurrencypairByPairId(matchedOrder.getPair().getPairId());
		logger.debug("currency pair: {}", currencyPair.getPairName());
		String[] tickters = new String[2];
		// finding the currency abbreviations
		tickters[0] = currencyPair.getToCurrency().get(0).getCurrencyAbbreviation();
		tickters[1] = currencyPair.getPairedCurrency().get(0).getCurrencyAbbreviation();
		// fetching the limit price of order
		String qtr = walletService.getPairedBalance(matchedOrder, currencyPair, qtyTraded);
		logger.debug("paired currency volume: {} {}", GenericUtils.getDecimalFormatString(Double.valueOf(qtr)),
				tickters[1]);
		// checking the order type BUY
		if (OrderType.BUY.equals(orders.getOrderType())) {
			logger.debug("BUY Order");

			msg = "Hi " + buyer.getFirstName() + ", Your " + orders.getOrderType()
					+ " order has been initiated, quantity: " + GenericUtils.getDecimalFormatString(qtyTraded) + " "
					+ tickters[0] + ", on " + GenericUtils.getDecimalFormatString(Double.valueOf(qtr)) + " "
					+ tickters[1] + " remaining voloume: " + GenericUtils.getDecimalFormatString(remainingVolume) + " "
					+ tickters[0];
			logger.debug("Byuer's transaction initiated msg: {}", msg);

			msg1 = "Hi " + seller.getFirstName() + ", Your " + matchedOrder.getOrderType()
					+ " order has been initiated, quantity: " + GenericUtils.getDecimalFormatString(qtyTraded) + " "
					+ tickters[0] + ", on " + GenericUtils.getDecimalFormatString(Double.valueOf(qtr)) + " "
					+ tickters[1] + " remaining voloume: "
					+ GenericUtils.getDecimalFormatString(matchedOrder.getVolume()) + " " + tickters[0];

			logger.debug("Seller's transaction initiated msg: {}", msg1);
		} else {
			logger.debug("SELL Order");
			msg1 = "Hi " + seller.getFirstName() + ", Your " + orders.getOrderType()
					+ " order has been initiated, quantity: " + GenericUtils.getDecimalFormatString(qtyTraded) + " "
					+ tickters[0] + ", on " + GenericUtils.getDecimalFormatString(Double.valueOf(qtr)) + " "
					+ tickters[1] + " remaining voloume: " + GenericUtils.getDecimalFormatString(remainingVolume) + " "
					+ tickters[0];
			logger.debug("Seller's msg1: {}", msg1);
			msg = "Hi " + buyer.getFirstName() + ", Your " + matchedOrder.getOrderType()
					+ " order has been initiated, quantity: " + GenericUtils.getDecimalFormatString(qtyTraded) + " "
					+ tickters[0] + ", on " + GenericUtils.getDecimalFormatString(Double.valueOf(qtr)) + " "
					+ tickters[1] + " remaining voloume: "
					+ GenericUtils.getDecimalFormatString(matchedOrder.getVolume()) + " " + tickters[0];
			logger.debug("Byuer's msg: {}", msg);
		}

		if (qtr != null && Double.valueOf(qtr) > 0) {
			// process tx buyers and sellers
			// double buyerQty = GenericUtils.getDecimalFormat(qtyTraded -
			// sellerTradeFee);
			logger.debug("actual quantity buyer: {}, will get: {} {}", buyer.getFirstName(),
					GenericUtils.getDecimalFormatString(qtyTraded), tickters[0]);
			/**
			 * Seller performing transaction; to send ETH to buyer in case of
			 * ETH/BTC pair
			 */
			Future<Boolean> txStatus = performTransaction(tickters[0], qtyTraded, buyer, seller, false, trade.getId());
			try {
				if (txStatus.get()) {
					logger.debug("Seller: {} has performed tx to buyer:{} of amount: {} {}", seller.getEmailId(),
							buyer.getEmailId(), GenericUtils.getDecimalFormatString(qtyTraded), tickters[0]);
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
						matchedOrder.setLockedVolume(lockedVolRemaining);
						orderAsyncServices.saveOrder(matchedOrder);
						logger.debug("seller locked volume, unlocking completed amount: {}", qtyTraded);
					} else {
						double lockedVolRemaining = orders.getLockedVolume() - qtyTraded;
						logger.debug("seller unlock volume: {}, remaining locked volume: {} ",
								GenericUtils.getDecimalFormatString(qtyTraded),
								GenericUtils.getDecimalFormatString(lockedVolRemaining));
						orders.setLockedVolume(lockedVolRemaining);
						orderAsyncServices.saveOrder(orders);
						logger.debug("seller locked volume, unlocking completed amount: {}",
								GenericUtils.getDecimalFormatString(qtyTraded));
					}
					notificationService.sendNotification(seller, msg1);
					notificationService.saveNotification(seller, buyer, msg1);
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double sellerQty = GenericUtils.getDecimalFormat(Double.valueOf(qtr) - sellerTradeFee);
			// double sellerQty =
			// GenericUtils.getDecimalFormat(Double.valueOf(qtr));
			logger.debug("actual quantity seller will get: {} {}", GenericUtils.getDecimalFormatString(sellerQty),
					tickters[1]);
			/**
			 * Buyer performing transaction; to send BTC to Seller in case of
			 * ETH/BTC pair
			 */
			txStatus = performTransaction(tickters[1], sellerQty, seller, buyer, false, trade.getId());

			try {
				if (txStatus.get()) {
					logger.debug("Buyer: {} has performed tx to Seller:{} of amount: {} {}", buyer.getEmailId(),
							seller.getEmailId(), GenericUtils.getDecimalFormatString(sellerQty), tickters[1]);
					trade.setIsTxBuyer(true);
					trade = orderAsyncServices.saveTrade(trade);
					logger.debug("Buyer tx perfrom status saved: {}", trade.getIsTxBuyer());
					/**
					 * unlocking locked volume
					 */
					logger.debug("buyer locked volume, unlocking started");
					if (OrderType.BUY.equals(orders.getOrderType())) {
						double lockedVolRemaining = orders.getLockedVolume() - (matchedOrder.getPrice() * qtyTraded);
						logger.debug("buyer unlock volume: {}, remaining locked volume: {} ",
								GenericUtils.getDecimalFormatString(matchedOrder.getPrice() * qtyTraded),
								GenericUtils.getDecimalFormatString(lockedVolRemaining));
						orders.setLockedVolume(lockedVolRemaining);
						orderAsyncServices.saveOrder(orders);
						logger.debug("buyer locked volume, unlocking completed for amount: {}",
								GenericUtils.getDecimalFormatString(matchedOrder.getPrice() * qtyTraded));
					} else {
						double lockedVolRemaining = matchedOrder.getLockedVolume()
								- (matchedOrder.getPrice() * qtyTraded);
						logger.debug("buyer unlock volume: {}, remaining locked volume: {} ",
								GenericUtils.getDecimalFormatString(matchedOrder.getPrice() * qtyTraded),
								GenericUtils.getDecimalFormatString(lockedVolRemaining));
						matchedOrder.setLockedVolume(lockedVolRemaining);
						orderAsyncServices.saveOrder(matchedOrder);
						logger.debug("buyer locked volume, unlocking completed amount: {}",
								GenericUtils.getDecimalFormatString(matchedOrder.getPrice() * qtyTraded));
					}
					notificationService.sendNotification(buyer, msg);
					notificationService.saveNotification(buyer, seller, msg);
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// fee deduction for admin
			User admin = userService.findByEmail(adminEmail);
			/*
			 * Future<Boolean> feeStatus; logger.debug("actual quantity admin
			 * will get from seller: {} {} of trade Id: {} ",
			 * GenericUtils.getDecimalFormat(sellerTradeFee), tickters[0],
			 * trade.getId()); feeStatus = performTransaction(tickters[0],
			 * sellerTradeFee, admin, seller, true); boolean res =
			 * feeStatus.get(); if (res) { trade.setIsFeeDeductedSeller(true);
			 * logger.debug("Set seller trade fee is deducted: {}",
			 * trade.getSellerTradeFee());
			 * logger.debug("Saving trade fee for seller started"); trade =
			 * orderAsyncServices.saveTrade(trade);
			 * logger.debug("Saving trade fee for seller completed: {}",
			 * trade.getSellerTradeFee()); }
			 */
			double tfee = GenericUtils.getDecimalFormat(buyerTradeFee + sellerTradeFee);
			logger.debug(
					"actual quantity admin will get from buyer: {} and seller: {} total fee: {} {} of trade Id: {} ",
					GenericUtils.getDecimalFormatString(buyerTradeFee),
					GenericUtils.getDecimalFormatString(sellerTradeFee), GenericUtils.getDecimalFormatString(tfee),
					tickters[1], trade.getId());
			Future<Boolean> feeStatus = performTransaction(tickters[1], tfee, admin, buyer, true, trade.getId());
			boolean res = false;
			try {
				res = feeStatus.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (res) {
				trade.setIsFeeDeductedBuyer(true);
				trade.setIsFeeDeductedSeller(true);
				if (trade.getIsTxBuyer() && trade.getIsTxSeller()) {
					trade.setStatus(true);
				}
				logger.debug("Set buyer trade fee is deducted: {}", trade.getIsFeeDeductedBuyer());
				logger.debug("Saving trade fee for buyer started");
				trade = orderAsyncServices.saveTrade(trade);
				logger.debug("Saving trade fee for buyer completed: {}", trade.getIsFeeDeductedBuyer());
			}
		} else {
			logger.debug("transaction processing failed due to paired currency volume");
		}
		return new AsyncResult<Boolean>(true);
	}

}