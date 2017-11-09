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
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;

import com.bolenum.dto.common.CurrencyForm;
import com.bolenum.enums.CurrencyType;
import com.bolenum.model.Currency;
import com.bolenum.model.Erc20Token;
import com.bolenum.model.User;
import com.bolenum.repo.admin.Erc20TokenRepository;
import com.bolenum.util.CryptoUtil;
import com.bolenum.util.Erc20TokenWrapper;
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
	
	private static final Logger logger = LoggerFactory.getLogger(Erc20TokenServiceImpl.class);
	
	@Override
	public Long countErc20Token() {
		return currencyService.countCourencies();
	}

	@Override
	public Erc20Token saveToken(Erc20Token erc20Token) {
		Erc20Token existingToken = erc20TokenRepository.findByContractAddress(erc20Token.getContractAddress());
		if (existingToken == null) {
			CurrencyForm currencyForm = new CurrencyForm(erc20Token.getCurrency().getCurrencyName(), erc20Token.getCurrency().getCurrencyAbbreviation(), CurrencyType.ERC20TOKEN);
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
	
	private Credentials getCredentials(User user) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, CipherException {
		File file = new File(ethWalletLocation);
		File jsonFile = new File(file+ "/" + user.getEthWalletJsonFileName());
		logger.debug("JSON file of the user is: {}", user.getEthWalletJsonFileName());
		String passwordKey = user.getEthWalletPwdKey();
		String decPwd = CryptoUtil.decrypt(user.getEthWalletPwd(), passwordKey);
		logger.debug("Decrypted password of the user is: {}", decPwd);
		return WalletUtils.loadCredentials(decPwd, jsonFile);
	}

	@Override
	public Double getErc20WalletBalance(User user, String tokenName) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, CipherException, InterruptedException, ExecutionException {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Double amount = 0.0;
		Credentials credentials = getCredentials(user);
		logger.debug("Requested token name is: {}", tokenName);
		Erc20Token erc20Token = getByCoin(tokenName);
		logger.debug("Contract address of the currency is: {}", erc20Token.getContractAddress());
		Erc20TokenWrapper token = Erc20TokenWrapper.load(erc20Token.getContractAddress(), web3j,
				credentials, BigInteger.valueOf(4700000), BigInteger.valueOf(3100000));
		amount = token.balanceOf(new Address(user.getEthWalletaddress())).get().getValue().doubleValue();
		logger.debug("Balance of the user is: {}", amount);
		return amount;
	}
	
	@Override
	public TransactionReceipt transferErc20Token(User user, String tokenName, String toAddress, Double fund) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, CipherException, TransactionException {
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		
		Erc20Token erc20Token = getByCoin(tokenName);
		BigInteger fundInBig = new BigDecimal(fund).toBigInteger();
		Uint256 transferFunds = new Uint256(fundInBig);
			Credentials credentials = getCredentials(user);
			Erc20TokenWrapper token = Erc20TokenWrapper.load(erc20Token.getContractAddress(), web3j,
					credentials, BigInteger.valueOf(4700000), BigInteger.valueOf(3100000));
		return token.transferFrom( new Address(user.getEthWalletaddress()), new Address(toAddress), transferFunds);
	}
	
}
