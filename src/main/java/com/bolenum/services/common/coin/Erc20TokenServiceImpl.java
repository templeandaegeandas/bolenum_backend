package com.bolenum.services.common.coin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.enums.CurrencyType;
import com.bolenum.enums.TransactionStatus;
import com.bolenum.enums.TransactionType;
import com.bolenum.enums.TransferStatus;
import com.bolenum.model.Currency;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.coin.Erc20Token;
import com.bolenum.model.coin.UserCoin;
import com.bolenum.repo.common.coin.Erc20TokenRepository;
import com.bolenum.repo.common.coin.UserCoinRepository;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.user.wallet.EtherumWalletService;
import com.bolenum.util.CryptoUtil;
import com.bolenum.util.Erc20TokenWrapper;
import com.bolenum.util.Erc20TokenWrapper.TransferEventResponse;
import com.bolenum.util.EthereumServiceUtil;
import com.bolenum.util.GenericUtils;

/**
 * 
 * @author Vishal Kumar
 * @date 04-Oct-2017
 *
 */
@Service
public class Erc20TokenServiceImpl implements Erc20TokenService {

	@Value("${bolenum.ethwallet.location}")
	private String ethWalletLocation;

	@Value("${admin.email}")
	private String adminEmail;

	@Autowired
	private Erc20TokenRepository erc20TokenRepository;

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private TransactionRepo transactionRepo;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocaleService localeService;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private UserCoinRepository userCoinRepository;

	@Autowired
	private EtherumWalletService etherumWalletService;

	private static final Logger logger = LoggerFactory.getLogger(Erc20TokenServiceImpl.class);

	@Override
	public Long countErc20Token() {
		return currencyService.countCourencies();
	}

	@Override
	@Async
	public void createErc20Wallet(User user, String tokenName) {
		File file = new File(ethWalletLocation);
		String fileName;
		UserCoin savedUserCoin = userCoinRepository.findByTokenNameAndUser(tokenName, user);
		if (savedUserCoin == null) {
			try {
				String password = UUID.randomUUID().toString().replaceAll("-", "");
				fileName = WalletUtils.generateFullNewWalletFile(password, file);
				logger.debug("wallet file name {} for: {}", fileName, tokenName);
				String passwordKey = CryptoUtil.getSecretKey();
				logger.debug("wallet file passwordKey: {}", passwordKey);
				String encPwd = CryptoUtil.encrypt(password, passwordKey);
				logger.debug("wallet file encPwd: {}", encPwd);
				File jsonFile = new File(file + File.separator + fileName);
				logger.debug("wallet file jsonFile: {}", jsonFile);
				Credentials credentials = WalletUtils.loadCredentials(password, jsonFile);
				logger.debug("wallet address: {}", credentials.getAddress());
				UserCoin userCoin = new UserCoin(credentials.getAddress(), 0.0, tokenName, fileName, encPwd,
						passwordKey, CurrencyType.ERC20TOKEN, user);
				userCoin = userCoinRepository.save(userCoin);
				List<UserCoin> userCoins = new ArrayList<>();
				userCoins.add(userCoin);
				user.setUserCoin(userCoins);
				User savedUser = userRepository.save(user);
				if (savedUser != null) {
					logger.debug("eth wallet info saved of user: {}", savedUser.getFullName());
				} else {
					logger.error("eth wallet info not saved of user");
				}

			} catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
					| NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException
					| CipherException | IOException e) {
				logger.error("Exception: {}", e);
			}
		}
	}

	@Override
	public UserCoin erc20WalletBalance(User user, Erc20Token erc20Token) {
		UserCoin userCoin = userCoinRepository
				.findByTokenNameAndUser(erc20Token.getCurrency().getCurrencyAbbreviation(), user);
		if (userCoin == null) {
			createErc20Wallet(user, erc20Token.getCurrency().getCurrencyAbbreviation());
		}
		return userCoin;
	}

	@Override
	public Erc20Token saveToken(Erc20Token erc20Token) {
		Erc20Token existingToken = erc20TokenRepository.findByContractAddress(erc20Token.getContractAddress());
		if (existingToken == null) {
			Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
			ClientTransactionManager transactionManager = new ClientTransactionManager(web3j,
					erc20Token.getContractAddress());
			Erc20TokenWrapper token = Erc20TokenWrapper.load(erc20Token.getContractAddress(), web3j, transactionManager,
					Contract.GAS_PRICE, Contract.GAS_LIMIT);
			Double decimalValue = null;
			try {
				decimalValue = createDecimals(token.decimals().getValue().intValue());
				logger.debug("Decimal value of this contract is: {}", decimalValue);
			} catch (Exception e) {
				return null;
			}
			erc20Token.setDecimalValue(decimalValue);
			CurrencyForm currencyForm = new CurrencyForm(erc20Token.getCurrency().getCurrencyName().toUpperCase(),
					erc20Token.getCurrency().getCurrencyAbbreviation().toUpperCase(), CurrencyType.ERC20TOKEN);
			Currency savedCurrency = currencyService.saveCurrency(currencyForm.copy(new Currency()));
			erc20Token.setCurrency(savedCurrency);
			return erc20TokenRepository.save(erc20Token);
		} else {
			throw new PersistenceException(localeService.getMessage("erc.token.already.exist"));
		}
	}

	@Override
	public Page<Erc20Token> listAllErc20Token(int pageNumber, int pageSize, String sortBy, String sortOrder) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return erc20TokenRepository.findByIsDeleted(false, pageRequest);
	}

	@Override
	public Erc20Token getById(Long id) {
		return erc20TokenRepository.findOne(id);
	}

	@Override
	public Erc20Token getByCoin(String coin) {
		return erc20TokenRepository.findByCurrencyCurrencyAbbreviation(coin);
	}

	@Override
	public void saveInitialErc20Token(List<Erc20Token> erc20Tokens) {
		erc20TokenRepository.save(erc20Tokens);
	}

	private Credentials getCredentials(UserCoin userCoin) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, CipherException {
		File file = new File(ethWalletLocation);
		File jsonFile = new File(file + File.separator + userCoin.getWalletJsonFile());
		logger.debug("JSON file of the user is: {}", userCoin.getWalletJsonFile());
		String passwordKey = userCoin.getWalletPwdKey();
		String decPwd = CryptoUtil.decrypt(userCoin.getWalletPwd(), passwordKey);
		logger.debug("Decrypted password of the user is: {}", decPwd);
		return WalletUtils.loadCredentials(decPwd, jsonFile);
	}

	@Override
	public Double getErc20WalletBalance(User user, Erc20Token erc20Token, String tokenName) {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Double amount = 0.0;
		Credentials credentials;
		Erc20TokenWrapper token = null;
		try {
			UserCoin userCoin = userCoinRepository.findByTokenNameAndUser(tokenName, user);
			credentials = getCredentials(userCoin);
			logger.debug("Requested token name is: {}", erc20Token.getCurrency().getCurrencyAbbreviation());
			logger.debug("Contract address of the currency is: {}", erc20Token.getContractAddress());
			token = Erc20TokenWrapper.load(erc20Token.getContractAddress(), web3j, credentials, Contract.GAS_PRICE,
					Contract.GAS_LIMIT);
			logger.debug("contract loaded");
			amount = token.balanceOf(new Address(userCoin.getWalletAddress())).getValue().doubleValue();
			Double am = amount / createDecimals(token.decimals().getValue().intValue());
			logger.debug("Balance: {} {} of the user: {}", GenericUtils.getDecimalFormatString(am),
					erc20Token.getCurrency().getCurrencyAbbreviation(), user.getEmailId());
			return amount / createDecimals(token.decimals().getValue().intValue());
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | IOException | CipherException e) {
			logger.debug("User getting balance for: {} failed", erc20Token.getCurrency().getCurrencyAbbreviation());
			return amount;
		}
	}

	@Override
	public Double getErc20WalletBalanceTemp(User user, Erc20Token erc20Token) {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Double amount = 0.0;
		Credentials credentials;
		Erc20TokenWrapper token = null;
		try {
			logger.debug("Requested token name is: {}", erc20Token.getCurrency().getCurrencyAbbreviation());
			File file = new File(ethWalletLocation);
			File jsonFile = new File(file + File.separator + user.getEthWalletJsonFileName());
			logger.debug("JSON file of the user is: {}", user.getEthWalletJsonFileName());
			String passwordKey = user.getEthWalletPwdKey();
			String decPwd = CryptoUtil.decrypt(user.getEthWalletPwd(), passwordKey);
			logger.debug("Decrypted password of the user is: {}", decPwd);
			credentials = WalletUtils.loadCredentials(decPwd, jsonFile);
			logger.debug("Contract address of the currency is: {}", erc20Token.getContractAddress());
			token = Erc20TokenWrapper.load(erc20Token.getContractAddress(), web3j, credentials, Contract.GAS_PRICE,
					Contract.GAS_LIMIT);
			logger.debug("contract loaded");
			amount = token.balanceOf(new Address(user.getEthWalletaddress())).getValue().doubleValue();
			logger.debug("Balance of the user is: {}", GenericUtils.getDecimalFormatString(amount));
			return amount / createDecimals(token.decimals().getValue().intValue());
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | IOException | CipherException e) {
			logger.error("User getting balance :{}", e);
			logger.debug("User getting balance for: {} failed", erc20Token.getCurrency().getCurrencyAbbreviation());
			return amount;
		}
	}

	@Override
	public TransactionReceipt transferErc20Token(User user, Erc20Token erc20Token, String toAddress, Double fund,
			String tokenName) {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		UserCoin userCoin = userCoinRepository.findByTokenNameAndUser(tokenName, user);
		TransactionReceipt receipt = null;
		try {
			Credentials credentials = getCredentials(userCoin);
			logger.debug("Credentials created of the user: {}", user.getEmailId());
			Erc20TokenWrapper token = Erc20TokenWrapper.load(erc20Token.getContractAddress(), web3j, credentials,
					Contract.GAS_PRICE, Contract.GAS_LIMIT);
			logger.debug("Transfering amount in Double: {}", token.decimals().getValue().intValue());
			BigInteger fundInBig = BigDecimal.valueOf(fund * createDecimals(token.decimals().getValue().intValue()))
					.toBigInteger();
			logger.debug("Transfering amount in BigInteger: {}", fundInBig);
			Uint256 transferFunds = new Uint256(fundInBig);
			logger.debug("Transfering amount in Unit256: {}", transferFunds);
			logger.debug("Contract loaded with credentials: {}", erc20Token.getContractAddress());
			logger.debug("To address: {}", toAddress);
			receipt = token.transfer(new Address(toAddress), transferFunds);
			logger.debug("Fund transfer transaction hash: {}", receipt.getTransactionHash());
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | IOException | CipherException | TransactionException e) {
			logger.error("{} transfer Erc20Token failed: {}", tokenName, e);
		}
		return receipt;
	}

	private double getEstimetedFeeErc20Token() {
		BigInteger bigIntegerFee = Contract.GAS_PRICE.multiply(Contract.GAS_LIMIT);
		double fee = GenericUtils.convertWeiToEther(bigIntegerFee);
		logger.debug("BLN transaction fee would be: {}", fee);
		return fee;
	}

	@Override
	public void saveIncomingErc20Transaction(String tokenName) throws IOException, CipherException {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Erc20Token erc20Token = getByCoin(tokenName);
		ClientTransactionManager transactionManager = new ClientTransactionManager(web3j,
				erc20Token.getContractAddress());
		Erc20TokenWrapper token = Erc20TokenWrapper.load(erc20Token.getContractAddress(), web3j, transactionManager,
				Contract.GAS_PRICE, Contract.GAS_LIMIT);
		token.transferEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
				.subscribe(tx -> {
					if (tx._to.getValue() != null) {
						UserCoin userCoin = userCoinRepository.findByWalletAddress(tx._to.getValue());
						if (userCoin != null) {
							logger.debug("new Incoming {} transaction for user : {}", tokenName,
									userCoin.getUser().getEmailId());
							saveTx(userCoin.getUser(), tx, tokenName, erc20Token);
						}
					}
				}, err -> logger.error("Erc20Token incoming transaction saving subscribe error: {}", err));
	}

	private void saveTx(User toUser, TransferEventResponse transaction, String tokenName, Erc20Token erc20Token) {
		Transaction tx = transactionRepo.findByTxHash(transaction._transactionHash);
		if (tx == null) {
			tx = new Transaction();
			logger.debug("saving transaction listener for user: {}", toUser.getEmailId());
			tx.setTxHash(transaction._transactionHash);
			tx.setFromAddress(transaction._from.getValue());
			tx.setToAddress(transaction._to.getValue());
			tx.setTransactionStatus(TransactionStatus.DEPOSIT);
			tx.setTransactionType(TransactionType.INCOMING);
			logger.debug("receiver user : {}", toUser);
			logger.debug("receiver user id: {}", toUser.getUserId());
			tx.setToUser(toUser);
			if (erc20Token != null) {
				tx.setTxAmount(transaction._value.getValue().doubleValue() / erc20Token.getDecimalValue());
			}
			tx.setCurrencyName(tokenName);
			tx.setTransferStatus(TransferStatus.INITIATED);
			if (toUser.getUserId() == 1) {
				tx.setTransferStatus(TransferStatus.COMPLETED);
			}
			Transaction saved = transactionRepo.save(tx);
			if (saved != null) {
				logger.debug("new incoming transaction saved of user: {} and hash: {}", saved.getFromAddress(),
						saved.getTxHash());
			}
		}
		simpMessagingTemplate.convertAndSend(
				UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_USER + "/" + toUser.getUserId(),
				com.bolenum.enums.MessageType.DEPOSIT_NOTIFICATION);
		logger.debug("message sent to websocket: {}", com.bolenum.enums.MessageType.DEPOSIT_NOTIFICATION);
	}

	Double createDecimals(int decimal) {
		int constant = 10;
		double ans = 1;
		for (int i = decimal; i > 0; i--) {
			ans *= constant;
		}
		return Double.valueOf(ans);
	}

	@Override
	public void sendUserTokenToAdmin() {
		Transaction transaction = transactionRepo.findFirstByTransactionStatusAndTransferStatusOrderByCreatedOnAsc(
				TransactionStatus.DEPOSIT, TransferStatus.INITIATED);
		if (transaction == null) {
			return;
		}
		List<Transaction> transactions = transactionRepo
				.findByToUserAndCurrencyNameAndTransactionStatusAndTransferStatus(transaction.getToUser(),
						transaction.getCurrencyName(), TransactionStatus.DEPOSIT, TransferStatus.INITIATED);
		transactions.forEach(transact -> transact.setTransferStatus(TransferStatus.PROCESSING));
		transactionRepo.save(transactions);
		double totalBalance = transactions.stream().filter(tran -> (tran.getTxAmount() > 0))
				.mapToDouble(Transaction::getTxAmount).sum();
		logger.debug("total transaction balance: {} of user: {}", totalBalance, transaction.getToUser().getEmailId());
		Erc20Token erc20Token = erc20TokenRepository.findByCurrencyCurrencyAbbreviation(transaction.getCurrencyName());
		User admin = userRepository.findByEmailId(adminEmail);
		UserCoin adminCoin = userCoinRepository.findByTokenNameAndUser("ETH", admin);
		if (erc20Token != null && CurrencyType.ERC20TOKEN.equals(erc20Token.getCurrency().getCurrencyType())) {
			UserCoin userCoin = userCoinRepository.findByTokenNameAndUser(transaction.getCurrencyName(),
					transaction.getToUser());
			logger.debug("userErc20Token: {}", userCoin);
			Double estimattedFee = getEstimetedFeeErc20Token();
			Boolean result = performEthTransaction(adminCoin, userCoin.getWalletAddress(), estimattedFee,
					TransactionStatus.TRANSFER, estimattedFee, transaction.getCurrencyName());
			transactions.forEach(transact -> transact.setTransferStatus(TransferStatus.PENDING));
			transactionRepo.save(transactions);
			if (result) {
				Double balance = getErc20WalletBalance(transaction.getToUser(), erc20Token,
						transaction.getCurrencyName());
				logger.debug("wallet balance is: {}", balance);
				userCoin.setBalance(userCoin.getBalance() + totalBalance);
				userCoinRepository.save(userCoin);
				logger.debug("saved!");
				transferErc20Token(userCoin.getUser(), erc20Token, adminCoin.getWalletAddress(), balance,
						transaction.getCurrencyName());
				transactions.forEach(transact -> transact.setTransferStatus(TransferStatus.COMPLETED));
				transactionRepo.save(transactions);
			}
		}
		if ("ETH".equals(transaction.getCurrencyName())) {
			UserCoin userCoin = userCoinRepository.findByTokenNameAndUser(transaction.getCurrencyName(),
					transaction.getToUser());
			Double balance = etherumWalletService.getEthWalletBalanceForAdmin(userCoin);
			Double estFee = GenericUtils.getEstimetedFeeEthereum();
			if (balance > 0) {
				boolean res = performEthTransaction(userCoin, adminCoin.getWalletAddress(), balance - estFee,
						TransactionStatus.TRANSFER, estFee, transaction.getCurrencyName());
				if (res) {
					transactions.forEach(transact -> transact.setTransferStatus(TransferStatus.COMPLETED));
					userCoin.setBalance(userCoin.getBalance() + totalBalance);
					userCoinRepository.save(userCoin);
					transactionRepo.save(transactions);
				}
			}
		}
	}

	/**
	 * 
	 * @description performEthTransaction
	 *
	 */
	private Boolean performEthTransaction(UserCoin userCoin, String toAddress, Double amount,
			TransactionStatus transactionStatus, Double fee, String currencyName) {
		logger.debug("performing eth transaction: {} to address: {}, amount: {}", userCoin.getUser().getEmailId(),
				toAddress, GenericUtils.getDecimalFormatString(amount));
		String passwordKey = userCoin.getWalletPwdKey();
		logger.debug("password key: {}", passwordKey);
		String fileName = ethWalletLocation + userCoin.getWalletJsonFile();
		logger.debug("user eth wallet file name: {}", fileName);
		File walletFile = new File(fileName);
		try {
			String decrPwd = CryptoUtil.decrypt(userCoin.getWalletPwd(), passwordKey);
			logger.debug("ETH transaction credentials load started");
			Credentials credentials = WalletUtils.loadCredentials(decrPwd, walletFile);
			logger.debug("ETH transaction credentials load completed");
			TransactionReceipt ethSendTransaction = transferEth(credentials, toAddress, amount);
			String txHash = null;
			if (ethSendTransaction != null && ethSendTransaction.getTransactionHash() != null) {
				txHash = ethSendTransaction.getTransactionHash();
			}
			logger.debug("eth transaction hash:{} of user: {}, amount: {}", txHash, userCoin.getUser().getEmailId(),
					amount);
			if (txHash == null) {
				return false;
			}
			Transaction transaction = transactionRepo.findByTxHash(txHash);
			logger.debug("transaction by hash: {}", transaction);
			if (transaction == null) {
				transaction = new Transaction();
				transaction.setTxHash(txHash);
				transaction.setFromAddress(userCoin.getWalletAddress());
				transaction.setToAddress(toAddress);
				transaction.setTxAmount(amount);
				transaction.setTransactionType(TransactionType.OUTGOING);
				if (TransactionStatus.TRANSFER.equals(transactionStatus)) {
					transaction.setTransactionStatus(transactionStatus);
					transaction.setTransferFeeCurrency(currencyName);
				}

				transaction.setTransferStatus(TransferStatus.COMPLETED);
				transaction.setFromUser(userCoin.getUser());
				transaction.setCurrencyName("ETH");
				if (fee != null) {
					transaction.setFee(fee);
				}
				User receiverUser = userRepository.findByEthWalletaddress(toAddress);
				if (receiverUser != null) {
					transaction.setToUser(receiverUser);
				}
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				if (saved != null) {
					logger.debug("transaction saved successfully of user: {}", userCoin.getUser().getEmailId());
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("ETH transaction failed:  {}", e);
		}
		return false;
	}

	private TransactionReceipt transferEth(Credentials credentials, String toAddress, Double amount) {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		try {
			return Transfer.sendFunds(web3j, credentials, toAddress, BigDecimal.valueOf(amount), Convert.Unit.ETHER)
					.send();
		} catch (Exception e) {
			logger.error("ethereum transaction failed: {}", e);
			return null;
		}
	}
}