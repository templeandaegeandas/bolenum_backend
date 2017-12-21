/**
 * 
 */
package com.bolenum.services.user.wallet;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.bolenum.constant.UrlConstant;
import com.bolenum.enums.TransactionStatus;
import com.bolenum.enums.TransactionType;
import com.bolenum.exceptions.InsufficientBalanceException;
import com.bolenum.model.Currency;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.coin.Erc20Token;
import com.bolenum.model.coin.UserCoin;
import com.bolenum.model.fees.WithdrawalFee;
import com.bolenum.repo.common.coin.UserCoinRepository;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.common.coin.Erc20TokenService;
import com.bolenum.services.order.book.OrdersService;
import com.bolenum.services.user.trade.TradeTransactionService;
import com.bolenum.services.user.transactions.TransactionService;
import com.bolenum.util.GenericUtils;
import com.bolenum.util.ResourceUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.AddressInfo;

/**
 * @author chandan kumar singh
 * @date 22-Sep-2017
 */
@Service
public class BTCWalletServiceImpl implements BTCWalletService {

	private static final Logger logger = LoggerFactory.getLogger(BTCWalletServiceImpl.class);

	@Autowired
	private Erc20TokenService erc20TokenService;

	@Autowired
	private OrdersService ordersService;

	@Autowired
	private TransactionRepo transactionRepo;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private LocaleService localeService;

	@Autowired
	private EtherumWalletService etherumWalletService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private UserCoinRepository userCoinRepository;

	@Value("${bitcoin.service.url}")
	private String btcUrl;

	@Value("${admin.email}")
	private String adminEmail;
	@Autowired
	private TradeTransactionService tradeTransactionService;

	/**
	 * 
	 * used to create hot wallet for Bitcoin
	 * 
	 * creating BIP32 hierarchical deterministic (HD) wallets
	 * 
	 * @param wallet
	 *            Id
	 * @return wallet Id
	 */
	@Override
	@Deprecated
	public String createHotWallet(String uuid) {
		String url = btcUrl + UrlConstant.HOT_WALLET;
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<>();
		logger.debug("create wallet uuid:  {}", uuid);
		parametersMap.add("uuid", uuid);
		Map<String, Object> map = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = restTemplate.postForObject(url, parametersMap, String.class);
			map = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
			});
			boolean isError = (boolean) map.get("error");
			logger.debug("create wallet isError:  {}", isError);
			if (!isError) {
				return uuid;
			}
		} catch (RestClientException e) {
			logger.error("create wallet exception RCE:  {}", e.getMessage());
		} catch (JsonParseException e) {
			logger.error("create wallet exception JPE:  {}", e.getMessage());
		} catch (JsonMappingException e) {
			logger.error("create wallet exception JME:  {}", e.getMessage());
		} catch (IOException e) {
			logger.error("create wallet exception IOE:  {}", e.getMessage());
		}
		return "";
	}

	/**
	 * to get Bitcoin wallet balance, depricated code
	 */

	@SuppressWarnings("unchecked")
	@Deprecated
	@Override
	public String getWalletBalance(String uuid) {
		String url = btcUrl + UrlConstant.WALLET_BAL;
		RestTemplate restTemplate = new RestTemplate();
		logger.debug("get Wallet balance:  {}", uuid);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("walletUuid", uuid);
		try {
			Map<String, Object> res = restTemplate.getForObject(builder.toUriString(), Map.class);
			String balance = (String) res.get("data");
			balance = balance.replace("BTC", "");
			return balance;
		} catch (RestClientException e) {
			logger.error("get Wallet balance RCE:  {}", e.getMessage());
		}
		return "";
	}

	/**
	 * to get bitcoin wallet address
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public String getWalletAddress(String walletUuid) {
		String url = btcUrl + UrlConstant.WALLET_ADDR;
		RestTemplate restTemplate = new RestTemplate();
		logger.debug("get Wallet Address uuid:  {}", walletUuid);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("walletUuid", walletUuid);
		try {
			Map<String, Object> res = restTemplate.getForObject(builder.toUriString(), Map.class);
			logger.debug("get Wallet Address res map: {}", res);
			boolean isError = (boolean) res.get("error");
			if (!isError) {
				Map<String, Object> data = (Map<String, Object>) res.get("data");
				return (String) data.get("address");
			}
		} catch (RestClientException e) {
			logger.error("get Wallet Address And QrCode exception RCE:  {}", e.getMessage());
		}
		return "";
	}

	/**
	 * to vaidate address of user wallet
	 */
	@Override
	@Deprecated
	public boolean validateAddresss(String btcWalletUuid, String toAddress) {
		RestTemplate restTemplate = new RestTemplate();
		String url = btcUrl + UrlConstant.WALLET_ADDR;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		ResponseEntity<String> txRes = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		logger.debug("Transaction response: {}", txRes);
		return false;
	}

	/**
	 * used to validate available balance in wallet in case of doing transaction
	 * during trading and withdrawal
	 * 
	 * @param availableBalance
	 * @param availableBalanceLimitToWithdraw
	 * @param withdrawAmount
	 * @return
	 */
	@Override
	public boolean validateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount,
			WithdrawalFee withdrawalFee, Currency currency, String toAddress) {
		Double availableBalance;
		Double minWithdrawAmount = 0.0;
		if (withdrawalFee != null) {
			minWithdrawAmount = withdrawalFee.getMinWithDrawAmount();
		}
		logger.debug("Minimum withdraw ammount:{}  of currency: {}", minWithdrawAmount, currency.getCurrencyName());

		UserCoin userCoin = userCoinRepository.findByTokenNameAndUser(tokenName, user);

		if (userCoin == null) {
			return false;
		}
		if (toAddress.equals(userCoin.getWalletAddress())) {
			throw new InsufficientBalanceException(localeService.getMessage("withdraw.own.wallet"));
		}

		if (withdrawAmount < minWithdrawAmount) {
			throw new InsufficientBalanceException(localeService.getMessage("min.withdraw.balance"));
		}
		if ("BTC".equals(tokenName)) {
			String balance = getBtcAccountBalance(user.getBtcWalletUuid());
			availableBalance = Double.valueOf(balance);
		} else {
			UserCoin ethUserCoin = etherumWalletService.ethWalletBalance(user, tokenName);
			availableBalance = ethUserCoin.getBalance();

			// networkFee = GenericUtils.getEstimetedFeeEthereum();
			// /**
			// * network fee required for sending to the receiver address and
			// * admin address, so networkFee = networkFee * 2;
			// *
			// */
			// networkFee = networkFee * 2;
		}
		// logger.debug("Available balance: {} estimeted network fee: {}",
		// availableBalance,
		// GenericUtils.getDecimalFormat().format(networkFee));
		// availableBalance = GenericUtils.getDecimalFormat(availableBalance -
		// lockVolume);
		logger.debug("Available balance after lock volume deduction: {} ", availableBalance);

		double placeOrderVolume = ordersService.totalUserBalanceInBook(user, currency, currency);
		logger.debug("Order Book balance:{} of user: {}", placeOrderVolume, user.getEmailId());
		double volume = GenericUtils.getDecimalFormat(withdrawAmount + placeOrderVolume);
		logger.debug("addition of withdraw amount, place order, fee and network fee volume: {}", volume);
		if (availableBalance >= volume) {
			return true;
		} else {
			throw new InsufficientBalanceException(MessageFormat
					.format(localeService.getMessage("insufficient.balance"), withdrawAmount, placeOrderVolume));
		}
	}

	@Override
	public boolean validateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount,
			WithdrawalFee withdrawalFee, String toAddress) {
		Double bolenumFee = 0.0;
		Double lockVolume = 0.0;
		Double minWithdrawAmount = 0.0;
		Double availableBalance;
		if (withdrawalFee != null) {
			bolenumFee = withdrawalFee.getFee();
			lockVolume = withdrawalFee.getLockVolume();
			minWithdrawAmount = withdrawalFee.getMinWithDrawAmount();
		}

		if (withdrawAmount <= bolenumFee) {
			throw new InsufficientBalanceException(localeService.getMessage("withdraw.balance.more.than.fee"));
		}
		/**
		 * network fee required for sending to the receiver address and admin
		 * address, so networkFee = networkFee * 2;
		 * 
		 */
		Erc20Token erc20Token = erc20TokenService.getByCoin(tokenName);
		UserCoin userErc20Token = erc20TokenService.erc20WalletBalance(user, erc20Token);
		if (userErc20Token == null) {
			return false;
		}
		if (toAddress.equals(userErc20Token.getWalletAddress())) {
			throw new InsufficientBalanceException(localeService.getMessage("withdraw.own.wallet"));
		}
		if (minWithdrawAmount != null && withdrawAmount < minWithdrawAmount) {
			throw new InsufficientBalanceException(localeService.getMessage("min.withdraw.balance"));
		}
		availableBalance = userErc20Token.getBalance();
		logger.debug("Available balance: {}", availableBalance);

		availableBalance = availableBalance - lockVolume;
		logger.debug("Available balance after lock volume deduction: {} ", availableBalance);

		double placeOrderVolume = ordersService.totalUserBalanceInBook(user, erc20Token.getCurrency(),
				erc20Token.getCurrency());

		logger.debug("Order Book balance: {} of user: {}", placeOrderVolume, user.getEmailId());

		double volume = withdrawAmount + placeOrderVolume;
		logger.debug("addition of withdraw amount, place order and fee volume: {}", volume);

		if (availableBalance >= volume) {
			return true;
		} else {
			throw new InsufficientBalanceException(MessageFormat.format(
					localeService.getMessage("insufficient.balance"), withdrawAmount, lockVolume, placeOrderVolume));
		}
	}

	/**
	 *  
	 */
	@Override
	public Transaction setDepositeList(Transaction transaction) {
		Transaction savedTransaction = transactionRepo.findByTxHash(transaction.getTxHash());
		logger.debug("savedTransaction {}", savedTransaction);
		User toUser = userRepository.findByBtcWalletAddress(transaction.getToAddress());
		if (savedTransaction == null) {
			transaction.setTransactionType(TransactionType.INCOMING);
			transaction.setTransactionStatus(TransactionStatus.DEPOSIT);
			transaction.setCurrencyName("BTC");
			transaction.setToUser(toUser);
			simpMessagingTemplate.convertAndSend(
					UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + toUser.getUserId(),
					com.bolenum.enums.MessageType.DEPOSIT_NOTIFICATION);
			return transactionRepo.saveAndFlush(transaction);
		} else {
			savedTransaction.setTransactionType(TransactionType.INCOMING);
			savedTransaction.setToUser(toUser);
			simpMessagingTemplate.convertAndSend(
					UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + toUser.getUserId(),
					com.bolenum.enums.MessageType.DEPOSIT_NOTIFICATION);
			return transactionRepo.saveAndFlush(savedTransaction);
		}
	}

	@Override
	@Async
	public Future<Boolean> withdrawAmount(String currencyType, String coinCode, User user, String toAddress,
			Double amount, Double bolenumFee, User admin) {
		switch (currencyType) {
		case "CRYPTO":
			switch (coinCode) {
			case "BTC":
				boolean result = transactionService.withdrawBTC(user, coinCode, toAddress, amount, bolenumFee);
				if (result) {
					if (bolenumFee > 0) {
						tradeTransactionService.performBtcTrade(user, admin, bolenumFee, null);
					}
					return new AsyncResult(true);
				}
				break;
			case "ETH":
				transactionService.performEthTransaction(user, toAddress, amount, TransactionStatus.WITHDRAW,
						bolenumFee, null);
				break;
			}
			break;
		case "ERC20TOKEN":
			boolean result = transactionService.withdrawErc20Token(user, coinCode, toAddress, amount,
					TransactionStatus.WITHDRAW, bolenumFee, null);
			if (result) {
				return new AsyncResult<>(true);
			}
			break;
		default:
			return new AsyncResult<>(false);
		}
		return new AsyncResult<>(false);
	}

	@Override
	public boolean adminWithdrawCryptoAmount(User user, String tokenName, Double withdrawAmount, String toAddress) {
		if ("BTC".equals(tokenName)) {
			transactionService.performBtcTransaction(user, toAddress, withdrawAmount, TransactionStatus.WITHDRAW, 0.0,
					null);
			return true;
		} else if ("ETH".equals(tokenName)) {
			transactionService.performEthTransaction(user, toAddress, withdrawAmount, TransactionStatus.WITHDRAW, 0.0,
					null);
			return true;
		}
		return false;
	}

	@Override
	public Future<Boolean> adminWithdrawErc20TokenAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		return transactionService.performErc20Transaction(user, tokenName, toAddress, withdrawAmount,
				TransactionStatus.WITHDRAW, 0.0, null);
	}

	@Override
	public boolean adminValidateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		Double availableBalance;
		UserCoin userCoin = userCoinRepository.findByTokenNameAndUser(tokenName, user);
		if (userCoin == null) {
			return false;
		}
		if (toAddress.equals(userCoin.getWalletAddress())) {
			throw new InsufficientBalanceException(localeService.getMessage("withdraw.own.wallet"));
		}
		if ("BTC".equals(tokenName)) {
			String balance = getBtcAccountBalance(user.getBtcWalletUuid());
			availableBalance = Double.valueOf(balance);
		} else {
			availableBalance = etherumWalletService.getEthWalletBalanceForAdmin(userCoin);
		}
		logger.debug("Available balance after lock volume deduction: {} ", availableBalance);

		double volume = GenericUtils.getDecimalFormat(withdrawAmount);
		logger.debug("addition of withdraw amount, place order, fee and network fee volume: {}", volume);
		if (availableBalance >= volume) {
			return true;
		} else {
			throw new InsufficientBalanceException(
					MessageFormat.format(localeService.getMessage("insufficient.balance"), withdrawAmount, 0.0));
		}
	}

	@Override
	public boolean adminValidateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress, Erc20Token erc20Token) {
		UserCoin userErc20Token = userCoinRepository.findByWalletAddress(toAddress);
		if (toAddress.equals(user.getEthWalletaddress())) {
			throw new InsufficientBalanceException(localeService.getMessage("withdraw.own.wallet"));
		}
		if (userErc20Token != null) {
			throw new InsufficientBalanceException(localeService.getMessage("withdraw.in.app.wallet"));
		}
		Double adminWalletBalance = erc20TokenService.getErc20WalletBalance(user, erc20Token, tokenName);
		if (adminWalletBalance < withdrawAmount) {
			throw new InsufficientBalanceException(localeService.getMessage("withdraw.invalid.available.balance"));
		}
		return true;
	}

	/**
	 * to create account in btc wallet of a user
	 * 
	 * @param userid
	 * @return btc address
	 */
	@Override
	public String createBtcAccount(String uuid) {
		try {
			BtcdClient client = ResourceUtils.getBtcdProvider();
			return client.getAccountAddress(uuid);
		} catch (BitcoindException | CommunicationException e) {
			logger.error("BTC account creation error: {}", e);
		}
		return null;
	}

	/**
	 * to get the user account balance
	 * 
	 * @param userid
	 * @return user account balance
	 */
	@Override
	public String getBtcAccountBalance(String uuid) {
		try {
			BtcdClient client = ResourceUtils.getBtcdProvider();
			BigDecimal balance = client.getBalance(uuid);
			String bal = GenericUtils.getDecimalFormatString(balance.doubleValue());
			logger.info("btc balance: {} of user: {} ", bal, uuid);
			return bal;
		} catch (BitcoindException | CommunicationException e) {
			logger.error("BTC account balance error: {}", e);
		}
		return "0";
	}

	/**
	 * to get the Bolenum server account balance
	 * 
	 * @return server account balance
	 */
	@Override
	public String getBolenumBtcAccountBalance() {
		try {
			BtcdClient client = ResourceUtils.getBtcdProvider();
			String bal = GenericUtils.getDecimalFormatString(client.getBalance().doubleValue());
			logger.info("btc balance of bolenum: {} ", bal);
			return bal;
		} catch (BitcoindException | CommunicationException e) {
			logger.error("BTC account balance error: {}", e);
		}
		return "0";
	}

	/**
	 * to get the account address of existing user
	 * 
	 * @return account address
	 */
	@Override
	public String getBtcAccountAddress(String walletUuid) {
		User user = GenericUtils.getLoggedInUser();
		UserCoin userCoin = userCoinRepository.findByTokenNameAndUser("BTC", user);
		if (userCoin == null) {
			return createBtcAccount(walletUuid);
		}
		return userCoin.getWalletAddress();
	}

	@Override
	public boolean validateBtcAddresss(String btcWalletUuid, String toAddress) {
		try {
			BtcdClient client = ResourceUtils.getBtcdProvider();
			AddressInfo address = client.validateAddress(toAddress);
			if (address.getIsValid()) {
				return true;
			}
		} catch (BitcoindException | CommunicationException e) {
			logger.error("validate adrress error: {}", e);
		}

		return false;
	}
}