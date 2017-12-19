package com.bolenum.services.common.erc20token;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.bolenum.constant.UrlConstant;
import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.enums.CurrencyType;
import com.bolenum.enums.TransactionStatus;
import com.bolenum.enums.TransactionType;
import com.bolenum.model.Currency;
import com.bolenum.model.Error;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.erc20token.Erc20Token;
import com.bolenum.model.erc20token.UserErc20Token;
import com.bolenum.repo.common.erc20token.Erc20TokenRepository;
import com.bolenum.repo.common.erc20token.UserErc20TokenRepository;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.user.ErrorService;
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
	private UserErc20TokenRepository userErc20TokenRepository;

	@Autowired
	private ErrorService errorService;

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
		UserErc20Token savedUserErc20Token = userErc20TokenRepository.findByTokenNameAndUser(tokenName, user);
		if (savedUserErc20Token == null) {
			try {
				String password = UUID.randomUUID().toString().replaceAll("-", "");
				fileName = WalletUtils.generateFullNewWalletFile(password, file);
				logger.debug("wallet file name {} for: {}", fileName, tokenName);
				String passwordKey = CryptoUtil.getSecretKey();
				logger.debug("wallet file passwordKey: {}", passwordKey);
				String encPwd = CryptoUtil.encrypt(password, passwordKey);
				logger.debug("wallet file encPwd: {}", encPwd);
				File jsonFile = new File(file + "/" + fileName);
				logger.debug("wallet file jsonFile: {}", jsonFile);
				Credentials credentials = WalletUtils.loadCredentials(password, jsonFile);
				logger.debug("wallet address: {}", credentials.getAddress());
				UserErc20Token userErc20Token = new UserErc20Token(credentials.getAddress(), 0.0, tokenName, fileName,
						encPwd, passwordKey, user);
				userErc20Token = userErc20TokenRepository.save(userErc20Token);
				List<UserErc20Token> userErc20Tokens = new ArrayList<>();
				userErc20Tokens.add(userErc20Token);
				user.setUserErc20Tokens(userErc20Tokens);
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
	public UserErc20Token erc20WalletBalance(User user, Erc20Token erc20Token) {
		UserErc20Token userErc20Token = userErc20TokenRepository
				.findByTokenNameAndUser(erc20Token.getCurrency().getCurrencyAbbreviation(), user);
		if (userErc20Token == null) {
			createErc20Wallet(user, erc20Token.getCurrency().getCurrencyAbbreviation());
		}
		return userErc20Token;
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

	private Credentials getCredentials(User user, String tokenName)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException, CipherException {
		File file = new File(ethWalletLocation);
		UserErc20Token userErc20Token = userErc20TokenRepository.findByTokenNameAndUser(tokenName, user);
		File jsonFile = new File(file + "/" + userErc20Token.getWalletJsonFile());
		logger.debug("JSON file of the user is: {}", userErc20Token.getWalletJsonFile());
		String passwordKey = userErc20Token.getWalletPwdKey();
		String decPwd = CryptoUtil.decrypt(userErc20Token.getWalletPwd(), passwordKey);
		logger.debug("Decrypted password of the user is: {}", decPwd);
		return WalletUtils.loadCredentials(decPwd, jsonFile);
	}

	public Double getErc20WalletBalance(UserErc20Token userErc20Token, Erc20Token erc20Token) {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Double amount = 0.0;
		Credentials credentials;
		Erc20TokenWrapper token = null;
		try {
			credentials = getCredentials(userErc20Token.getUser(), erc20Token.getCurrency().getCurrencyAbbreviation());
			logger.debug("Requested token name is: {}", erc20Token.getCurrency().getCurrencyAbbreviation());
			logger.debug("Contract address of the currency is: {}", erc20Token.getContractAddress());
			token = Erc20TokenWrapper.load(erc20Token.getContractAddress(), web3j, credentials, Contract.GAS_PRICE,
					Contract.GAS_LIMIT);
			logger.debug("contract loaded");
			amount = token.balanceOf(new Address(userErc20Token.getWalletAddress())).getValue().doubleValue();
			logger.debug("Balance of the user is: {}", GenericUtils.getDecimalFormatString(amount));
			return amount / createDecimals(token.decimals().getValue().intValue());
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | IOException | CipherException e) {
			logger.debug("User getting balance for: {} failed", erc20Token.getCurrency().getCurrencyAbbreviation());
			return null;
		}
	}

	@Override
	public TransactionReceipt transferErc20Token(User user, Erc20Token erc20Token, String toAddress, Double fund)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException, CipherException, TransactionException, InterruptedException,
			ExecutionException {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Credentials credentials = getCredentials(user, erc20Token.getCurrency().getCurrencyAbbreviation());
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
		TransactionReceipt receipt = token.transfer(new Address(toAddress), transferFunds);
		logger.debug("Fund transfer transaction hash: {}", receipt.getTransactionHash());
		return receipt;
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
					logger.debug("listning transactions: {}", tx._to.getValue());
					if (tx._to.getValue() != null) {
						UserErc20Token userErc20Token = userErc20TokenRepository.findByWalletAddress(tx._to.getValue());
						if (userErc20Token != null) {
							logger.debug("new Incoming {} transaction for user : {}", tokenName,
									userErc20Token.getUser().getEmailId());
							saveTx(userErc20Token.getUser(), tx, tokenName, erc20Token);
							
						}
					}
				}, err -> logger.error("Erc20Token incoming transaction saving subscribe error: {}", err.getMessage()));
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
			User senderUser = userRepository.findByBtcWalletAddress(transaction._from.getValue());
			logger.debug("receiver user : {}", toUser);
			logger.debug("receiver user id: {}", toUser.getUserId());
			tx.setToUser(toUser);
			if (erc20Token != null) {
				tx.setTxAmount(transaction._value.getValue().doubleValue() / erc20Token.getDecimalValue());
			}
			tx.setCurrencyName(tokenName);
			if (senderUser != null) {
				tx.setTransactionStatus(TransactionStatus.WITHDRAW);
				tx.setTransactionType(TransactionType.OUTGOING);
				logger.debug("from user id: {}", senderUser.getUserId());
				tx.setFromUser(senderUser);
			}
			Transaction saved = transactionRepo.save(tx);
			if (saved != null) {
				logger.debug("new incoming transaction saved of user: {} and hash: {}", saved.getFromAddress(),
						saved.getTxHash());
			}
		} else {
			logger.debug("saving else transaction listenr for user: {}", toUser.getEmailId());
			tx.setFromAddress(transaction._from.getValue());
			tx.setToAddress(transaction._to.getValue());
			if (erc20Token != null) {
				tx.setTxAmount(transaction._value.getValue().doubleValue() / erc20Token.getDecimalValue());
				logger.debug("Balance else part returned by the listner: {}",
						transaction._value.getValue().doubleValue() / erc20Token.getDecimalValue());
			}
			tx.setTransactionType(TransactionType.OUTGOING);
			if (TransactionStatus.WITHDRAW.equals(tx.getTransactionStatus())) {
				tx.setTransactionType(TransactionType.INCOMING);
			}
			tx.setCurrencyName(tokenName);
			logger.debug("from else user id: {}", toUser.getUserId());
			tx.setToUser(toUser);
			Transaction saved = transactionRepo.saveAndFlush(tx);
			if (saved != null) {
				logger.debug("new incoming transaction saved of user: {}", toUser.getEmailId());

			} else {
				if (tx.getTransactionStatus().equals(TransactionStatus.WITHDRAW)) {
					tx.setTransactionType(TransactionType.INCOMING);
				}
				logger.debug("tx exists: {}", transaction._transactionHash);
				transactionRepo.saveAndFlush(tx);
				logger.debug("new incoming else transaction saved of user: {}", toUser.getEmailId());
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
	
//	public void sendUserTokenToAdmin() {
//		Transaction transaction = transactionRepo.findByTxStatus(null);
//		Erc20Token erc20Token = erc20TokenRepository.findByCurrencyCurrencyAbbreviation(transaction.getCurrencyName());
//		UserErc20Token userErc20Token = userErc20TokenRepository.findByTokenNameAndUser(transaction.getCurrencyName(), transaction.getToUser());
//		logger.debug("userErc20Token: {}", userErc20Token);
//		userErc20Token.setBalance(userErc20Token.getBalance()
//				+ tx._value.getValue().doubleValue() / erc20Token.getDecimalValue());
//		userErc20TokenRepository.save(userErc20Token);
//		logger.debug("saved!");
//		User admin = userRepository.findByEmailId(adminEmail);
//		Boolean result = performEthTransaction(admin, userErc20Token.getWalletAddress(),
//				GenericUtils.getEstimetedFeeEthereum(), TransactionStatus.FEE, null, null);
//		try {
//			if (result) {
//				try {
//					Double balance = getErc20WalletBalance(userErc20Token, erc20Token);
//					logger.debug("wallet balance is: {}", balance);
//					transferErc20Token(userErc20Token.getUser(), erc20Token,
//							admin.getEthWalletaddress(), balance);
//				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
//						| IllegalBlockSizeException | BadPaddingException | IOException
//						| CipherException | TransactionException e) {
//					logger.error("{} transaction failed: {}", token, e.getMessage());
//					e.printStackTrace();
//				}
//			}
//		} catch (InterruptedException | ExecutionException e) {
//			logger.error("Ethereum transaction failed: {}", e.getMessage());
//		}
//	}

	private Boolean performEthTransaction(User fromUser, String toAddress, Double amount,
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
				return false;
			}
			logger.debug("ETH transaction send fund completed");
			String txHash = ethSendTransaction.getTransactionHash();
			logger.debug("eth transaction hash:{} of user: {}, amount: {}", txHash, fromUser.getEmailId(), amount);
			Transaction transaction = transactionRepo.findByTxHash(txHash);
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
					logger.debug("transaction saved successfully of user: {}", fromUser.getEmailId());
					return true;
				}
			}
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e1) {
			logger.error("ETH transaction failed:  {}", e1);
		}
		return false;
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
			logger.error("ethereum transaction failed: {}", e);
			return null;
		}
	}
}