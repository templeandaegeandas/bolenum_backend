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
import java.util.ArrayList;
import java.util.List;
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

import com.bolenum.enums.CurrencyType;
import com.bolenum.model.User;
import com.bolenum.model.coin.UserCoin;
import com.bolenum.repo.common.coin.UserCoinRepository;
import com.bolenum.repo.user.UserRepository;
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

	// @Autowired
	// private UserService userService;

	@Autowired
	private UserCoinRepository userCoinRepository;

	@Autowired
	private UserRepository userRepository;

	private static final Logger logger = LoggerFactory.getLogger(EtherumWalletServiceImpl.class);

	// @Override
	// @Async
	// public void createWallet(User user) {
	// File file = new File(ethWalletLocation);
	// String fileName;
	// try {
	// String password = UUID.randomUUID().toString().replaceAll("-", "");
	// fileName = WalletUtils.generateFullNewWalletFile(password, file);
	// logger.debug("wallet file name: {}", fileName);
	// String passwordKey = CryptoUtil.getSecretKey();
	// logger.debug("wallet file passwordKey: {}", passwordKey);
	// String encPwd = CryptoUtil.encrypt(password, passwordKey);
	// logger.debug("wallet file encPwd: {}", encPwd);
	// File jsonFile = new File(file + "/" + fileName);
	// logger.debug("wallet file jsonFile: {}", jsonFile);
	// Credentials credentials = WalletUtils.loadCredentials(password,
	// jsonFile);
	// logger.debug("wallet address: {}", credentials.getAddress());
	// user.setEthWalletaddress(credentials.getAddress());
	// user.setEthWalletPwdKey(passwordKey);
	// user.setEthWalletPwd(encPwd);
	// user.setEthWalletJsonFileName(fileName);
	// User savedUser = userService.saveUser(user);
	// if (savedUser != null) {
	// logger.debug("eth wallet info saved of user: {}",
	// savedUser.getFullName());
	// } else {
	// logger.error("eth wallet info not saved of user");
	// }
	//
	// } catch (InvalidKeyException e) {
	// logger.error("InvalidKeyException: {}", e.getMessage());
	// } catch (NoSuchPaddingException e) {
	// logger.error("NoSuchPaddingException: {}", e.getMessage());
	// } catch (IllegalBlockSizeException e) {
	// logger.error("IllegalBlockSizeException: {}", e.getMessage());
	// } catch (BadPaddingException e) {
	// logger.error("BadPaddingException: {}", e.getMessage());
	// } catch (NoSuchAlgorithmException e) {
	// logger.error("NoSuchAlgorithmException: {}", e.getMessage());
	// } catch (NoSuchProviderException e) {
	// logger.error("NoSuchProviderException: {}", e.getMessage());
	// } catch (InvalidAlgorithmParameterException e) {
	// logger.error("InvalidAlgorithmParameterException: {}", e.getMessage());
	// } catch (CipherException e) {
	// logger.error("CipherException: {}", e.getMessage());
	// } catch (IOException e) {
	// logger.error("IOException: {}", e.getMessage());
	// }
	// }

	@Override
	@Async
	public void createEthWallet(User user, String tokenName) {
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
						passwordKey, CurrencyType.CRYPTO, user);
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
	public String cleintVersion() {
		Web3j web3 = EthereumServiceUtil.getWeb3jInstance();
		Web3ClientVersion web3ClientVersion = null;
		try {
			web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
			String clientVersion = web3ClientVersion.getWeb3ClientVersion();
			logger.debug("web3 client version: {}", clientVersion);
			return clientVersion;
		} catch (InterruptedException | ExecutionException e) {
			logger.error("ethereum client version error: {}", e);
		}
		return "";
	}

	/**
	 * 
	 */
	@Override
	public Double getEthWalletBalanceForAdmin(UserCoin userCoin) {
		BigInteger amount = BigInteger.valueOf(0);
		try {
			amount = EthereumServiceUtil.getWeb3jInstance()
					.ethGetBalance(userCoin.getWalletAddress(), DefaultBlockParameterName.fromString("latest")).send()
					.getBalance();
			BigDecimal balance = new BigDecimal(amount);
			BigDecimal conversionRate = new BigDecimal(new BigInteger("1000000000000000000"));
			BigDecimal amountInEther = balance.divide(conversionRate);
			logger.debug("Ethtereum wallet balance: {} of user: {} ", amountInEther.doubleValue(),
					userCoin.getUser().getEmailId());
			return GenericUtils.getDecimalFormat(amountInEther.doubleValue());
		} catch (IOException e) {
			logger.error("get wallet balance error: {}", e.getMessage());
		}
		return amount.doubleValue();
	}

	/**
	 * 
	 */
	@Override
	public UserCoin ethWalletBalance(User user, String tokenName) {
		UserCoin existingUserCoin = userCoinRepository.findByTokenNameAndUser(tokenName, user);
		if (existingUserCoin == null) {
			createEthWallet(user, tokenName);
		}
		return existingUserCoin;
	}

}
