/**
 * 
 */
package com.bolenum.services.user.wallet;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import com.bolenum.model.User;
import com.bolenum.services.user.UserService;
import com.bolenum.util.CryptoUtil;
import com.bolenum.util.EthereumServiceUtil;
import com.bolenum.util.GenericUtils;

/**
 * @author chandan kumar singh
 * @date 26-Sep-2017
 */
@Service
public class EtherumWalletServiceImpl implements EtherumWalletService {
	@Value("${bolenum.ethwallet.location}")
	private String ethWalletLocation;

	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(EtherumWalletServiceImpl.class);

	@Override
	@Async
	public void createWallet(User user) {
		File file = new File(ethWalletLocation);
		String fileName;
		try {
			String password = UUID.randomUUID().toString().replaceAll("-", "");
			fileName = WalletUtils.generateFullNewWalletFile(password, file);
			logger.debug("wallet file name: {}", fileName);
			String passwordKey = CryptoUtil.getSecretKey();
			logger.debug("wallet file passwordKey: {}", passwordKey);
			String encPwd = CryptoUtil.encrypt(password, passwordKey);
			logger.debug("wallet file encPwd: {}", encPwd);
			File jsonFile = new File(file + "/" + fileName);
			logger.debug("wallet file jsonFile: {}", jsonFile);
			Credentials credentials = WalletUtils.loadCredentials(password, jsonFile);
			logger.debug("wallet address: {}", credentials.getAddress());
			user.setEthWalletaddress(credentials.getAddress());
			user.setEthWalletPwdKey(passwordKey);
			user.setEthWalletPwd(encPwd);
			user.setEthWalletJsonFileName(fileName);
			User savedUser = userService.saveUser(user);
			if (savedUser != null) {
				logger.debug("eth wallet info saved of user: {}", savedUser.getFullName());
			} else {
				logger.error("eth wallet info not saved of user");
			}

		} catch (InvalidKeyException e) {
			logger.error("InvalidKeyException: {}", e.getMessage());
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			logger.error("NoSuchPaddingException: {}", e.getMessage());
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			logger.error("IllegalBlockSizeException: {}", e.getMessage());
			e.printStackTrace();
		} catch (BadPaddingException e) {
			logger.error("BadPaddingException: {}", e.getMessage());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			logger.error("NoSuchAlgorithmException: {}", e.getMessage());
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			logger.error("NoSuchProviderException: {}", e.getMessage());
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			logger.error("InvalidAlgorithmParameterException: {}", e.getMessage());
			e.printStackTrace();
		} catch (CipherException e) {
			logger.error("CipherException: {}", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String cleintVersion() {
		Web3j web3 = EthereumServiceUtil.getWeb3jInstance();
		Web3ClientVersion web3ClientVersion = null;
		try {
			web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
			String clientVersion = web3ClientVersion.getWeb3ClientVersion();
			logger.debug("web3 client version: {}", clientVersion);
			return clientVersion;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public Double getWalletBalance(User user) {
		BigInteger amount = BigInteger.valueOf(0);
		if (user.getEthWalletaddress() == null) {
			logger.debug("user does not have ethtereum wallet address: {}", user.getEmailId());
			createWallet(user);
			return amount.doubleValue();
		}
		try {
			amount = EthereumServiceUtil.getWeb3jInstance()
					.ethGetBalance(user.getEthWalletaddress(), DefaultBlockParameterName.fromString("latest")).send()
					.getBalance();
			BigDecimal balance = new BigDecimal(amount);
			BigDecimal conversionRate = new BigDecimal(new BigInteger("1000000000000000000"));
			BigDecimal amountInEther = balance.divide(conversionRate);
			logger.debug("Ethtereum wallet balance: {} of user: {} ", amountInEther.doubleValue(), user.getEmailId());
			return GenericUtils.getDecimalFormat(amountInEther.doubleValue());
		} catch (IOException e) {
			logger.error("get wallet balance error: {}", e.getMessage());
			e.printStackTrace();
		}
		return amount.doubleValue();
	}
}
