package com.bolenum.services.admin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
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

import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.enums.CurrencyType;
import com.bolenum.enums.TransactionStatus;
import com.bolenum.enums.TransactionType;
import com.bolenum.model.Currency;
import com.bolenum.model.Erc20Token;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.repo.admin.Erc20TokenRepository;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.util.CryptoUtil;
import com.bolenum.util.Erc20TokenWrapper;
import com.bolenum.util.Erc20TokenWrapper.TransferEventResponse;
import com.bolenum.util.EthereumServiceUtil;

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

	@Autowired
	private Erc20TokenRepository erc20TokenRepository;

	@Autowired
	private CurrencyService currencyService;
	
	@Autowired
	private TransactionRepo transactionRepo;
	
	@Autowired
	private UserRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(Erc20TokenServiceImpl.class);

	@Override
	public Long countErc20Token() {
		return currencyService.countCourencies();
	}

	@Override
	public Erc20Token saveToken(Erc20Token erc20Token) {
		Erc20Token existingToken = erc20TokenRepository.findByContractAddress(erc20Token.getContractAddress());
		if (existingToken == null) {
			CurrencyForm currencyForm = new CurrencyForm(erc20Token.getCurrency().getCurrencyName(),
					erc20Token.getCurrency().getCurrencyAbbreviation(), CurrencyType.ERC20TOKEN);
			Currency savedCurrency = currencyService.saveCurrency(currencyForm.copy(new Currency()));
			erc20Token.setCurrency(savedCurrency);
			return erc20TokenRepository.save(erc20Token);
		} else {
			throw new PersistenceException("erc.token.already.exist");
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
		Page<Erc20Token> tokenList = erc20TokenRepository.findByIsDeleted(false, pageRequest);
		return tokenList;
	}

	@Override
	public Erc20Token getById(Long id) {
		return erc20TokenRepository.findOne(id);
	}

	public Erc20Token getByCoin(String coin) {
		return erc20TokenRepository.findByCurrencyCurrencyAbbreviation(coin);
	}

	@Override
	public void saveInitialErc20Token(List<Erc20Token> erc20Tokens) {
		erc20TokenRepository.save(erc20Tokens);
	}

	private Credentials getCredentials(User user) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, CipherException {
		File file = new File(ethWalletLocation);
		File jsonFile = new File(file + "/" + user.getEthWalletJsonFileName());
		logger.debug("JSON file of the user is: {}", user.getEthWalletJsonFileName());
		String passwordKey = user.getEthWalletPwdKey();
		String decPwd = CryptoUtil.decrypt(user.getEthWalletPwd(), passwordKey);
		logger.debug("Decrypted password of the user is: {}", decPwd);
		return WalletUtils.loadCredentials(decPwd, jsonFile);
	}

	@Override
	public Double getErc20WalletBalance(User user, String tokenName)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException, CipherException, InterruptedException, ExecutionException {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Double amount = 0.0;
		Credentials credentials = getCredentials(user);
		logger.debug("Requested token name is: {}", tokenName);
		Erc20Token erc20Token = getByCoin(tokenName);
		logger.debug("Contract address of the currency is: {}", erc20Token.getContractAddress());
		Erc20TokenWrapper token = Erc20TokenWrapper.load(erc20Token.getContractAddress(), web3j, credentials,
				Contract.GAS_PRICE, Contract.GAS_LIMIT);
		amount = token.balanceOf(new Address(user.getEthWalletaddress())).getValue().doubleValue();
		logger.debug("Balance of the user is: {}", amount);
		return amount/token.decimals().getValue().doubleValue();
	}

	@Override
	public TransactionReceipt transferErc20Token(User user, String tokenName, String toAddress, Double fund)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException, CipherException, TransactionException, InterruptedException, ExecutionException {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();

		Erc20Token erc20Token = getByCoin(tokenName);
		Credentials credentials = getCredentials(user);
		logger.debug("Credentials created of the user: {}", user.getEmailId());
		Erc20TokenWrapper token = Erc20TokenWrapper.load(erc20Token.getContractAddress(), web3j, credentials,
				Contract.GAS_PRICE, Contract.GAS_LIMIT);
		logger.debug("Transfering amount in Double: {}", token.decimals().getValue().doubleValue());
		BigInteger fundInBig = new BigDecimal(fund*token.decimals().getValue().doubleValue()).toBigInteger();
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
		token.transferEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST).subscribe(tx -> {
			if (tx._to.getValue() != null) {
				logger.debug("tx.getTo() {}",tx._to);
				User user = userRepository.findByEthWalletaddress(tx._to.getValue());
				if (user != null) {
					logger.debug("new Incoming ethereum transaction for user : {}", user.getEmailId());
					saveTx(user, tx, tokenName);
				}
			}
		});
	}
	
	private void saveTx(User user, TransferEventResponse transaction, String tokenName) {
		Transaction tx = transactionRepo.findByTxHash(transaction._transactionHash);
		if (tx == null) {
			tx = new Transaction();
			tx.setTxHash(transaction._transactionHash);
			tx.setFromAddress(transaction._from.getValue());
			tx.setToAddress(transaction._to.getValue());
			tx.setTxAmount(transaction._value.getValue().doubleValue()/10000000);
			tx.setTransactionType(TransactionType.INCOMING);
			tx.setTransactionStatus(TransactionStatus.DEPOSIT);
			tx.setCurrencyName(tokenName);
			tx.setUser(user);
			Transaction saved = transactionRepo.saveAndFlush(tx);
			if (saved != null) {
				logger.debug("new incoming transaction saved of user: {}", user.getEmailId());
			}
		} else {
			if (tx.getTransactionStatus().equals(TransactionStatus.WITHDRAW)) {
				tx.setTransactionType(TransactionType.INCOMING);
			}
			logger.debug("tx exists: {}", transaction._transactionHash);
			transactionRepo.saveAndFlush(tx);
		}
	}

}
