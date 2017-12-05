/**
 * 
 */
package com.bolenum.services.user.wallet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

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
import com.bolenum.model.Erc20Token;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.fees.WithdrawalFee;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.services.admin.Erc20TokenService;
import com.bolenum.services.admin.fees.WithdrawalFeeService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.order.book.OrdersService;
import com.bolenum.util.GenericUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private WithdrawalFeeService withdrawalFeeService;

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

	@Value("${bitcoin.service.url}")
	private String btcUrl;

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
	public String createHotWallet(String uuid) {
		String url = btcUrl + UrlConstant.HOT_WALLET;
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<String, String>();
		logger.debug("create wallet uuid:  {}", uuid);
		parametersMap.add("uuid", uuid);
		Map<String, Object> map = new HashMap<String, Object>();
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
			e.printStackTrace();
		} catch (JsonParseException e) {
			logger.error("create wallet exception JPE:  {}", e.getMessage());
			e.printStackTrace();
		} catch (JsonMappingException e) {
			logger.error("create wallet exception JME:  {}", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("create wallet exception IOE:  {}", e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * to get Bitcoin wallet balance
	 */
	@SuppressWarnings("unchecked")
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
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * to get bitcoin wallet address
	 */
	@SuppressWarnings("unchecked")
	@Override
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
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * to vaidate address of user wallet
	 */
	@Override
	public boolean validateAddresss(String btcWalletUuid, String toAddress) {
		RestTemplate restTemplate = new RestTemplate();
		String url = btcUrl + UrlConstant.WALLET_ADDR;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
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
			WithdrawalFee withdrawalFee, Currency currency) {
		Double availableBalance = 0.0, minWithdrawAmount = 0.0, bolenumFee = 0.0, networkFee = 0.0;
		if (withdrawalFee != null) {
			minWithdrawAmount = withdrawalFee.getMinWithDrawAmount();
			bolenumFee = withdrawalFee.getFee();
		}
		logger.debug("Minimum withdraw ammount:{} Withdraw fee: {} of currency: {}", minWithdrawAmount, bolenumFee,
				currency.getCurrencyName());
		if (withdrawAmount < minWithdrawAmount) {
			throw new InsufficientBalanceException(localeService.getMessage("min.withdraw.balance"));
		}
		if ("BTC".equals(tokenName)) {
			String balance = getWalletBalance(user.getBtcWalletUuid());
			availableBalance = Double.valueOf(balance);
		} else {
			availableBalance = etherumWalletService.getWalletBalance(user);
			networkFee = GenericUtils.getEstimetedFeeEthereum();
		}
		logger.debug("Available balance: {} estimeted network fee: {}", availableBalance,
				GenericUtils.getDecimalFormat().format(networkFee));
		double placeOrderVolume = ordersService.totalUserBalanceInBook(user, currency, currency);
		logger.debug("Order Book balance:{} of user: {}", placeOrderVolume, user.getEmailId());
		if (availableBalance >= (withdrawAmount + placeOrderVolume + bolenumFee + networkFee)) {
			return true;
		} else {
			throw new InsufficientBalanceException(MessageFormat
					.format(localeService.getMessage("insufficient.balance"), availableBalance, placeOrderVolume));
		}
	}

	@Override
	public boolean validateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount) {
		Double availableBalance = 0.0;
		Erc20Token erc20Token = erc20TokenService.getByCoin(tokenName);
		Double minWithdrawAmount = withdrawalFeeService.getWithdrawalFee(erc20Token.getCurrency().getCurrencyId())
				.getMinWithDrawAmount();

		if (minWithdrawAmount != null && withdrawAmount < minWithdrawAmount) {
			throw new InsufficientBalanceException(localeService.getMessage("min.withdraw.balance"));
		}
		availableBalance = erc20TokenService.getErc20WalletBalance(user, erc20Token);
		double placeOrderVolume = ordersService.totalUserBalanceInBook(user, erc20Token.getCurrency(),
				erc20Token.getCurrency());
		logger.debug("Available balance: {}", availableBalance);
		logger.debug("OrderBook balance of user: {}", placeOrderVolume);
		if (availableBalance >= (withdrawAmount + placeOrderVolume)) {
			return true;
		} else {
			throw new InsufficientBalanceException(MessageFormat
					.format(localeService.getMessage("insufficient.balance"), availableBalance, placeOrderVolume));
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
			simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_DEPOSIT,
					com.bolenum.enums.MessageType.DEPOSIT_NOTIFICATION);
			return transactionRepo.saveAndFlush(transaction);
		} else {
			savedTransaction.setTransactionType(TransactionType.INCOMING);
			savedTransaction.setToUser(toUser);
			simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_DEPOSIT,
					com.bolenum.enums.MessageType.DEPOSIT_NOTIFICATION);
			return transactionRepo.saveAndFlush(savedTransaction);
		}
	}
}
