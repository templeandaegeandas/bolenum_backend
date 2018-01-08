/**
 * 
 */
package com.bolenum.services.user.wallet;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.bolenum.constant.UrlConstant;
import com.bolenum.enums.CurrencyType;
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
		Double bolenumFee = 0.0;
		if (withdrawalFee != null) {
			bolenumFee = withdrawalFee.getFee();
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
		if (withdrawAmount <= bolenumFee) {
			throw new InsufficientBalanceException(localeService.getMessage("withdraw.balance.more.than.fee"));
		}
		if ("BTC".equals(tokenName)) {
			String balance = getBtcAccountBalance(user.getBtcWalletUuid());
			availableBalance = Double.valueOf(balance);
		} else {
			UserCoin ethUserCoin = etherumWalletService.ethWalletBalance(user, tokenName);
			availableBalance = ethUserCoin.getBalance();
		}

		double placeOrderVolume = ordersService.totalUserBalanceInBook(user, currency, currency);
		logger.debug("Order Book balance:{} of user: {}", placeOrderVolume, user.getEmailId());
		double lockedVolume = ordersService.findUserOrderLockedVolume(user, currency, currency);
		logger.debug("Order locked volume :{} of user : {} ", lockedVolume, user.getEmailId());
		double volume = placeOrderVolume + lockedVolume + withdrawAmount;
		logger.debug("addition of withdraw amount, place order, fee and network fee volume: {}",
				GenericUtils.getDecimalFormatString(volume));
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

		double lockedVolume = ordersService.findUserOrderLockedVolume(user, erc20Token.getCurrency(),
				erc20Token.getCurrency());
		logger.debug("Order locked volume :{} of user : {} ", lockedVolume, user.getEmailId());
		double volume = placeOrderVolume + lockedVolume + withdrawAmount;

		logger.debug("addition of withdraw amount, place order,lock order vol and fee volume: {}",
				GenericUtils.getDecimalFormatString(volume));

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
				boolean result = transactionService.withdrawBTC(user, coinCode, toAddress, amount - bolenumFee,
						bolenumFee);
				if (result) {
					if (bolenumFee > 0) {
						tradeTransactionService.performBtcTrade(user, admin, bolenumFee, null);
					}
					return new AsyncResult<>(true);
				}
				break;
			case "ETH":
				boolean resultForEth = transactionService.withdrawETH(user, coinCode, toAddress, amount, bolenumFee,
						null);
				if (resultForEth) {
					return new AsyncResult<>(true);
				}
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
			transactionService.withdrawBTC(user, tokenName, toAddress, withdrawAmount, null);
			return true;
		} else if ("ETH".equals(tokenName)) {
			transactionService.withdrawETH(user, tokenName, toAddress, withdrawAmount, 0.0, null);
			return true;
		}
		return false;
	}

	@Override
	public Future<Boolean> adminWithdrawErc20TokenAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		UserCoin toUserCoin = userCoinRepository.findByWalletAddress(toAddress);
		if (toUserCoin != null) {
			toUserCoin.setBalance(toUserCoin.getBalance() + withdrawAmount);
			UserCoin newUserCoin = userCoinRepository.save(toUserCoin);
			if (newUserCoin != null) {
				return new AsyncResult<>(true);
			}
			return new AsyncResult<>(false);
		} else {
			return transactionService.performErc20Transaction(user, tokenName, toAddress, withdrawAmount, 0.0, null);
		}
	}

	@Override
	public boolean adminValidateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		Double availableBalance;
		Double adminMaintainBal = 2.0;
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
			logger.debug("Available balance: {} {}", availableBalance, tokenName);
		} else {
			Double tranferFees = transactionService.totalTrasferFeePaidByAdmin(tokenName);
			if (tranferFees == null) {
				tranferFees = 0.0;
			}
			Double usersDepositBalance = userCoinRepository.findTotalDepositBalanceOfUser(tokenName);
			if (usersDepositBalance == null) {
				usersDepositBalance = 0.0;
			}

			availableBalance = etherumWalletService.getEthWalletBalanceForAdmin(userCoin);
			if (availableBalance == null) {
				availableBalance = 0.0;
			}
			availableBalance = availableBalance - (usersDepositBalance + tranferFees + adminMaintainBal);
			logger.debug(
					"Admin Available balance:{} Maintain Bal volume:{} , usersDepositBalance: {} and transfer fee: {} ",
					availableBalance, adminMaintainBal, usersDepositBalance, tranferFees);
		}

		if (availableBalance >= withdrawAmount) {
			return true;
		} else {
			throw new InsufficientBalanceException(MessageFormat
					.format(localeService.getMessage("admin.insufficient.balance"), withdrawAmount, adminMaintainBal));
		}
	}

	@Override
	public boolean adminValidateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress, Erc20Token erc20Token) {
		if (toAddress.equals(user.getEthWalletaddress())) {
			throw new InsufficientBalanceException(localeService.getMessage("withdraw.own.wallet"));
		}
		Double usersDepositBalance = userCoinRepository.findTotalDepositBalanceOfUser(tokenName);
		if (usersDepositBalance == null) {
			usersDepositBalance = 0.0;
		}
		Double adminWalletBalance = erc20TokenService.getErc20WalletBalance(user, erc20Token, tokenName);

		if (adminWalletBalance == null) {
			adminWalletBalance = 0.0;
		}
		adminWalletBalance = adminWalletBalance - usersDepositBalance;
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
		if (walletUuid.isEmpty()) {
			return createBtcAccount(walletUuid);
		}
		User user = GenericUtils.getLoggedInUser();
		UserCoin userCoin = userCoinRepository.findByTokenNameAndUser("BTC", user);
		if (userCoin == null) {
			String address = createBtcAccount(walletUuid);
			userCoin = new UserCoin();
			userCoin.setWalletAddress(address);
			userCoin.setTokenName("BTC");
			userCoin.setUser(user);
			userCoin.setCurrencyType(CurrencyType.CRYPTO);
			userCoin = userCoinRepository.save(userCoin);
			return userCoin.getWalletAddress();
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