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

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
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
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.coin.Erc20Token;
import com.bolenum.model.coin.UserCoin;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.common.coin.UserCoinRepository;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.services.common.coin.Erc20TokenService;
import com.bolenum.services.order.book.OrderAsyncService;
import com.bolenum.services.user.UserService;
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
	private Erc20TokenService erc20TokenService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

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

	private static final String STATUS = "CONFIRMED";

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
			Double fee, Long tradeId) {
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

			EthSendTransaction ethSendTransaction = transferEth(credentials, toAddress, amount);
			String txHash = null;
			if (ethSendTransaction != null && ethSendTransaction.getTransactionHash() != null) {
				txHash = ethSendTransaction.getTransactionHash();
				logger.debug("eth transaction hash:{} of user: {}, amount: {}", txHash, fromUser.getEmailId(), amount);
			} else {
				logger.debug("eth transaction hash:{} of user: {}, amount: {}", txHash, fromUser.getEmailId(), amount);
				return new AsyncResult<>(false);
			}
			Transaction transaction = transactionRepo.findByTxHash(txHash);
			logger.debug("transaction by hash: {}", transaction);
			if (transaction == null) {
				logger.debug("saving transaction for user: {}, hash: {}", fromUser.getEmailId(), txHash);
				transaction = new Transaction();
				transaction.setTxHash(txHash);
				transaction.setFromAddress(fromUserCoin.getWalletAddress());
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				transaction.setTransactionStatus(TransactionStatus.WITHDRAW);
				transaction.setTransferStatus(TransferStatus.COMPLETED);
				transaction.setFromUser(fromUser);
				transaction.setCurrencyName(tokenName);
				if (fee != null) {
					transaction.setFee(fee);
				}

				UserCoin receiverUserCoin = userCoinRepository.findByWalletAddress(toAddress);
				if (receiverUserCoin != null && receiverUserCoin.getUser() != null) {
					transaction.setToUser(receiverUserCoin.getUser());
				}
				transaction.setTradeId(tradeId);

				Transaction saved = transactionRepo.saveAndFlush(transaction);
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(
							UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + fromUser.getUserId(),
							com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("socket message sent to websocket: {}",
							com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("eth transaction saved successfully of user: {}", fromUser.getEmailId());

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

	@Override
	@Async
	public Future<Boolean> performErc20Transaction(User fromUser, String tokenName, String toAddress, Double amount,
			Double fee, Long tradeId) {
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
				transaction.setTransactionStatus(TransactionStatus.WITHDRAW);
				transaction.setTransferStatus(TransferStatus.COMPLETED);
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
				if (saved != null) {
					simpMessagingTemplate.convertAndSend(
							UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + fromUser.getUserId(),
							com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("message sent to websocket: {}", com.bolenum.enums.MessageType.WITHDRAW_NOTIFICATION);
					logger.debug("{} transaction saved successfully of user: {}", tokenName, fromUser.getEmailId());
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
	public Page<Transaction> getListOfUserTransaction(String currencyName, User user,
			TransactionStatus transactionStatus, int pageNumber, int pageSize, String sortOrder, String sortBy) {

		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		if (TransactionStatus.WITHDRAW.equals(transactionStatus)) {
			Page<Transaction> list = transactionRepo.findByFromUserAndTransactionStatusAndCurrencyName(user,
					transactionStatus, currencyName, pageRequest);
			fetchTransactionConfirmation(list);
			fetchBTCConfirmation(list);
			return list;
		} else {
			Page<Transaction> list = transactionRepo.findByToUserAndCurrencyNameAndTransactionStatusOrTransactionStatus(
					user, currencyName, pageRequest);
			fetchTransactionConfirmation(list);
			fetchBTCConfirmation(list);
			return list;
		}
	}

	@Override
	@Async
	public Future<Boolean> processTransaction(Orders matchedOrder, Orders orders, double qtyTraded, User buyer,
			User seller, double remainingVolume, double buyerTradeFee, double sellerTradeFee, Trade trade) {
		String currentThread = Thread.currentThread().getName();
		logger.debug("thread: {} going to sleep for 10 Secs ", currentThread);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			logger.error("{} got intruptted and exception: {}", currentThread, e);
			Thread.currentThread().interrupt();
		}
		logger.debug("thread: {} waked up after for 10 Secs ", currentThread);
		logger.debug("buyer: {} and seller: {} for order: {}", buyer.getEmailId(), seller.getEmailId(),
				matchedOrder.getId());
		Currency marketCurrency = matchedOrder.getMarketCurrency();
		Currency pairedCurrency = matchedOrder.getPairedCurrency();
		String toCurrAbrrivaiton = pairedCurrency.getCurrencyAbbreviation();
		String pairCurrAbrrivaiton = marketCurrency.getCurrencyAbbreviation();
		String toCurrencyType = pairedCurrency.getCurrencyType().toString();
		String pairCurrencyType = marketCurrency.getCurrencyType().toString();
		String qtr = walletService.getPairedBalance(matchedOrder, marketCurrency, pairedCurrency, qtyTraded);
		double pairedCurrencyVolume = Double.parseDouble(qtr);
		pairedCurrencyVolume = GenericUtils.getDecimalFormat(pairedCurrencyVolume);
		logger.debug("paired currency volume: {} {}", GenericUtils.getDecimalFormatString(pairedCurrencyVolume),
				pairCurrAbrrivaiton);
		if (pairedCurrencyVolume <= 0) {
			logger.debug("transaction processing failed due to paired currency volume of trade id :{}", trade.getId());
			return new AsyncResult<>(false);
		}
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
			logger.debug("seller tx perfromed status saved: {}", trade.getIsTxSeller());
			// unlocking locked volume
			unlockVolumeSeller(orders, matchedOrder, qtyTraded);
		}
		double sellerQty = GenericUtils.getDecimalFormat(pairedCurrencyVolume - sellerTradeFee);
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
			// unlocking locked volume
			unlockVolumeBuyer(orders, matchedOrder, qtyTraded, buyerTradeFee);

		}
		User admin = userService.findByEmail(adminEmail);
		double tfee = GenericUtils.getDecimalFormat(buyerTradeFee + sellerTradeFee);
		logger.debug("actual quantity admin will get from buyer: {} and seller: {} total fee: {} {} of trade Id: {} ",
				GenericUtils.getDecimalFormatString(buyerTradeFee), GenericUtils.getDecimalFormatString(sellerTradeFee),
				GenericUtils.getDecimalFormatString(tfee), pairCurrAbrrivaiton, trade.getId());
		boolean feeRes = tradeTransactionService.performTradeTransactionFee(pairCurrAbrrivaiton, pairCurrencyType, tfee,
				admin, buyer, trade.getId());
		logger.debug("trade fee transaction: {}", feeRes);
		if (trade.getIsTxBuyer() && trade.getIsTxSeller()) {
			trade.setStatus(true);
		}
		if (feeRes) {
			trade.setIsFeeDeductedBuyer(true);
			trade.setIsFeeDeductedSeller(true);
			logger.debug("Set buyer trade fee is deducted: {}", trade.getIsFeeDeductedBuyer());
			logger.debug("Saving trade fee for buyer started");
			trade = orderAsyncServices.saveTrade(trade);
			logger.debug("Saving trade fee for buyer completed: {}", trade.getIsFeeDeductedBuyer());
			// unlocking locked volume
			unlockVolumeBuyerFee(orders, matchedOrder, buyerTradeFee);
		}
		return new AsyncResult<>(true);
	}

	private void unlockVolumeBuyer(Orders orders, Orders matchedOrder, double qtyTraded, double buyerTradeFee) {
		/**
		 * unlocking locked volume of buyers
		 */
		logger.debug("buyer locked volume, unlocking started");
		if (OrderType.BUY.equals(orders.getOrderType())) {
			double uvol = matchedOrder.getPrice() * qtyTraded - buyerTradeFee;
			double lockedVolRemaining = orders.getLockedVolume() - uvol;
			logger.debug("buyer unlock volume: {}, remaining locked volume: {} ",
					GenericUtils.getDecimalFormatString(uvol), GenericUtils.getDecimalFormatString(lockedVolRemaining));
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
					GenericUtils.getDecimalFormatString(uvol), GenericUtils.getDecimalFormatString(lockedVolRemaining));
			if (lockedVolRemaining < 0) {
				lockedVolRemaining = 0;
			}
			matchedOrder.setLockedVolume(lockedVolRemaining);
			orderAsyncServices.saveOrder(matchedOrder);
			logger.debug("buyer locked volume, unlocking completed amount: {}",
					GenericUtils.getDecimalFormatString(uvol));
		}
	}

	private void unlockVolumeBuyerFee(Orders orders, Orders matchedOrder, double buyerTradeFee) {
		logger.debug("buyer fee locked volume, unlocking started");
		double uvol = buyerTradeFee * 2;
		if (OrderType.BUY.equals(orders.getOrderType())) {
			double lockedVolRemaining = orders.getLockedVolume() - uvol;
			logger.debug("buyer fee unlock volume: {}, remaining locked volume: {} ",
					GenericUtils.getDecimalFormatString(uvol), GenericUtils.getDecimalFormatString(lockedVolRemaining));
			if (lockedVolRemaining < 0) {
				lockedVolRemaining = 0;
			}
			orders.setLockedVolume(lockedVolRemaining);
			orderAsyncServices.saveOrder(orders);
			logger.debug("buyer fee locked volume, unlocking completed for amount: {}",
					GenericUtils.getDecimalFormatString(buyerTradeFee));
		} else {
			double lockedVolRemaining = matchedOrder.getLockedVolume() - uvol;
			logger.debug("buyer fee unlock volume: {}, remaining locked volume: {} ",
					GenericUtils.getDecimalFormatString(uvol), GenericUtils.getDecimalFormatString(lockedVolRemaining));
			if (lockedVolRemaining < 0) {
				lockedVolRemaining = 0;
			}
			matchedOrder.setLockedVolume(lockedVolRemaining);
			orderAsyncServices.saveOrder(matchedOrder);
			logger.debug("buyer locked volume, unlocking completed amount: {}",
					GenericUtils.getDecimalFormatString(uvol));
		}
	}

	private void unlockVolumeSeller(Orders orders, Orders matchedOrder, double qtyTraded) {
		/**
		 * unlocking locked volume of sellers
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

	/**
	 * 
	 */
	@Override
	public void fetchTransactionConfirmation(Page<Transaction> page) {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		List<Transaction> list = new ArrayList<>();

		page.forEach(transaction -> {
			try {
				if (!"BTC".equalsIgnoreCase(transaction.getCurrencyName()) && transaction.getTxHash() != null
						&& !STATUS.equals(transaction.getTxStatus()) && !transaction.isInAppTransaction()) {
					TransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transaction.getTxHash())
							.send().getResult();
					if (transactionReceipt != null) {
						BigInteger txBlockNumber = transactionReceipt.getBlockNumber();
						BigInteger ethBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
						BigInteger blockNumber = ethBlockNumber.subtract(txBlockNumber);
						logger.debug("number: {}", blockNumber);
						if (blockNumber.compareTo(BigInteger.valueOf(12)) > 0) {
							transaction.setNoOfConfirmations(12);
							transaction.setTxStatus(STATUS);
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
		page.forEach(transaction -> {
			if ("BTC".equalsIgnoreCase(transaction.getCurrencyName()) && transaction.getTxHash() != null
					&& !STATUS.equals(transaction.getTxStatus()) && !transaction.isInAppTransaction()) {

				try {
					BtcdClient btcdClient = ResourceUtils.getBtcdProvider();
					com.neemre.btcdcli4j.core.domain.Transaction transaction1 = btcdClient
							.getTransaction(transaction.getTxHash());
					if (transaction1.getConfirmations() >= 6) {
						transaction.setNoOfConfirmations(6);
						transaction.setTxStatus(STATUS);
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
			UserCoin receiverUserCoin = userCoinRepository.findByWalletAddress(toAddress);
			if (receiverUserCoin != null) {
				receiverUserCoin.setBalance(receiverUserCoin.getBalance() + (amount - fee));
				senderUserCoin.setBalance(senderUserCoin.getBalance() - amount);
				userCoinRepository.save(receiverUserCoin);
				userCoinRepository.save(senderUserCoin);
				return saveInAppTransaction(fromUser, senderUserCoin, receiverUserCoin, toAddress, tokenName,
						(amount - fee), fee);
			}
			Future<Boolean> txRes = performErc20Transaction(fromUser, tokenName, toAddress, amount - fee, fee, null);
			try {
				boolean res = txRes.get();
				logger.debug("withdraw {} token status: {}", tokenName, res);
				if (res) {
					senderUserCoin.setBalance(senderUserCoin.getBalance() - amount);
					userCoinRepository.save(senderUserCoin);
					return true;
				}
			} catch (InterruptedException | ExecutionException e) {
				logger.error("withdraw {} token error: {}", tokenName, e);
			}
		}
		return false;
	}

	/**
	 * @created by Himanshu Kumar to withdraw ETH
	 */
	@Override
	public boolean withdrawETH(User fromUser, String tokenName, String toAddress, Double amount, Double fee,
			Long tradeId) {
		UserCoin senderUserCoin = userCoinRepository.findByTokenNameAndUser(tokenName, fromUser);
		if (senderUserCoin != null) {
			UserCoin receiverUserCoin = userCoinRepository.findByWalletAddress(toAddress);
			if (receiverUserCoin != null) {
				receiverUserCoin.setBalance(receiverUserCoin.getBalance() + (amount - fee));
				userCoinRepository.save(receiverUserCoin);
				senderUserCoin.setBalance(senderUserCoin.getBalance() - amount);
				userCoinRepository.save(senderUserCoin);
				return saveInAppTransaction(fromUser, senderUserCoin, receiverUserCoin, toAddress, tokenName,
						(amount - fee), fee);
			}
			Future<Boolean> txRes = performEthTransaction(fromUser, tokenName, toAddress, amount - fee, fee, null);
			try {
				boolean res = txRes.get();
				logger.debug("withdraw eth status: {}", res);
				if (res) {
					senderUserCoin.setBalance(senderUserCoin.getBalance() - amount);
					userCoinRepository.save(senderUserCoin);
					return true;
				}
			} catch (InterruptedException | ExecutionException e) {
				logger.error("withdraw eth error: {}", e);
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
		transaction.setTransferStatus(TransferStatus.COMPLETED);
		transaction.setFromUser(fromUser);
		transaction.setCurrencyName(tokenName);
		transaction.setFee(fee);
		transaction.setToUser(receiverUserCoin.getUser());
		transaction.setInAppTransaction(true);
		transaction.setTxStatus(STATUS);
		Transaction saved = transactionRepo.saveAndFlush(transaction);
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

	@Override
	public Double totalTrasferFeePaidByAdmin(String currencyName) {
		Double balance = transactionRepo.totalTrasferFeePaidByAdmin(currencyName);
		return balance == null ? 0.0 : balance;
	}

	@Override
	public Page<Transaction> getListOfTransferTransaction(int pageNumber, int pageSize, String sortOrder,
			String sortBy) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return transactionRepo.findByTransactionStatus(TransactionStatus.TRANSFER, pageRequest);
	}
}