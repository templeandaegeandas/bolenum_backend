package com.bolenum.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;

import com.bolenum.enums.CurrencyType;
import com.bolenum.model.Countries;
import com.bolenum.model.Currency;
import com.bolenum.model.Privilege;
import com.bolenum.model.Role;
import com.bolenum.model.States;
import com.bolenum.model.User;
import com.bolenum.model.coin.Erc20Token;
import com.bolenum.model.fees.TradingFee;
import com.bolenum.model.fees.WithdrawalFee;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.admin.fees.TradingFeeService;
import com.bolenum.services.admin.fees.WithdrawalFeeService;
import com.bolenum.services.common.CountryAndStateService;
import com.bolenum.services.common.PrivilegeService;
import com.bolenum.services.common.RoleService;
import com.bolenum.services.common.coin.Erc20TokenService;
import com.bolenum.services.user.UserService;
import com.bolenum.services.user.wallet.BTCWalletService;
import com.bolenum.services.user.wallet.EtherumWalletService;
import com.bolenum.util.PasswordEncoderUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author chandan kumar singh
 * @date 12-Sep-2017
 */
@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {
	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private PrivilegeService privilegeService;

	@Autowired
	private Erc20TokenService erc20TokenService;

	@Autowired
	private PasswordEncoderUtil passwordEncoder;

	@Autowired
	private Environment environment;

	@Autowired
	private CountryAndStateService countriesAndStateService;

	@Value("${bolenum.ethwallet.location}")
	private String ethWalletLocation; // ethereum wallet file location

	@Value("${bolenum.profile.image.location}")
	private String userProfileImageLocation;

	@Value("${bolenum.document.location}")
	private String userDocumetsLocation;

	@Value("${bolenum.google.qr.code.location}")
	private String googleQrCodeLocation;

	@Value("${bolenum.deployed.contract.address}")
	private String contractAddress;

	@Value("${bolenum.deployed.contract.wallet.address}")
	private String walletAddress;

	@Value("${bolenum.deployed.contract.currency.name}")
	private String currencyName;

	@Value("${bolenum.deployed.contract.currency.abbreviation}")
	private String currencyAbbreviation;

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private EtherumWalletService etherumWalletService;

	@Autowired
	@Lazy
	private BTCWalletService btcWalletService;

	@Autowired
	private TradingFeeService tradingFeeService;

	@Autowired
	private WithdrawalFeeService withdrawalFeeService;

	private Set<Privilege> privileges = new HashSet<>();

	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		addPrivileges();
		addRole();
		createAdmin();
		saveCountries();
		saveStates();

		saveCurrency();
		saveInitialErc20Tokens();
		saveInitialCurrencyPair();
		try {
			erc20TokenService.saveIncomingErc20Transaction(currencyAbbreviation);
		} catch (IOException | CipherException e) {
			logger.error("error saving incoming erc20 token txns: {}", e);

		}

		// create initial directories
		createInitDirectories();
		createProfilePicDirectories();
		createDocumentsDirectories();
		createGoogleAuthQrCodeDirectories();
		saveInitialFee();
	}

	/**
	 * this method save initial fee at the time of application start
	 * @param Nothing.
	 * @return void.
	 */
	private void saveInitialFee() {
		TradingFee fee = tradingFeeService.getTradingFee();
		if (fee == null) {
			fee = new TradingFee();
			fee.setFee(0.15);
			fee.setFiat(0.0);
			tradingFeeService.saveTradingFee(fee);
		}
	}

	/**
	 * this method create ethereum wallet location at the time of application
	 * start @description createInitDirectories 
	 * @param Nothing.
	 * @return void.
	 */
	private void createDocumentsDirectories() {
		Path profileImg = Paths.get(userDocumetsLocation);

		if (!profileImg.toFile().exists()) {
			if (new File((userDocumetsLocation)).mkdirs()) {
				logger.debug("Documents location created");
			} else {
				logger.debug("Documents location creation failed");
			}
		} else {
			logger.debug("Documents location exists");
		}
	}

	/**
	 * This method create GoogleAuthQrCodeDirectories.
	 * @param Nothing.
	 * @return void. 
	 */
	private void createGoogleAuthQrCodeDirectories() {
		Path profileImg = Paths.get(googleQrCodeLocation);

		if (!profileImg.toFile().exists()) {
			if (new File((googleQrCodeLocation)).mkdirs()) {
				logger.debug("Documents location created");
			} else {
				logger.debug("Documents location creation failed");
			}
		} else {
			logger.debug("Documents location exists");
		}

	}

	/**
	 * This method create ethereum wallet location at the time of application start.
	 * @param Nothing.
	 * @return void. 
	 */
	private void createInitDirectories() {
		Path ethWallet = Paths.get(ethWalletLocation);
		if (!ethWallet.toFile().exists()) {
			if (new File((ethWalletLocation)).mkdirs()) {
				logger.debug("ethereum wallet location created");
			} else {
				logger.debug("ethereum wallet location creation failed");
			}
		} else {
			logger.debug("ethereum wallet location exists");
		}

	}

	/**
	 * This method create ProfilePicDirectories.
	 * @param Nothing.
	 * @return void. 
	 */
	private void createProfilePicDirectories() {
		Path profileImg = Paths.get(userProfileImageLocation);

		if (!profileImg.toFile().exists()) {
			if (new File((userProfileImageLocation)).mkdirs()) {
				logger.debug("User Profile Image location created");
			} else {
				logger.debug("User Profile Image location creation failed");
			}
		} else {
			logger.debug("User Profile Image location exists");
		}
	}

	/**
	 * This method add Role. 
	 * @param Nothing
	 * @return void
	 */
	private void addRole() {
		Role role = new Role("ROLE_USER", "user role", privilegeService.findAllPrevileges());
		roleService.findOrCreate(role);
	}

	/**
	 * This method add Privileges 
	 * @param Nothing.
	 * @return void
	 */
	private void addPrivileges() {
		Privilege add = new Privilege("add", "adding");
		Privilege edit = new Privilege("edit", "editing");
		Privilege get = new Privilege("view", "showing");
		Privilege del = new Privilege("delete", "deleting");
		privileges.add(add);
		privileges.add(edit);
		privileges.add(get);
		privileges.add(del);
		privileges.forEach(privilege -> privilegeService.findOrCreate(privilege));
	}

	/**
	 * This method createAdmin. 
	 * @param Nothing.
	 * @return void.
	 */
	private void createAdmin() {
		Role r = new Role("ROLE_ADMIN", "Admin role", privilegeService.findAllPrevileges());
		Role roleAdmin = roleService.findOrCreate(r);
		User admin = userService.findByEmail("admin@bolenum.com");
		String activeProfile = environment.getActiveProfiles()[0];
		logger.debug("Currently active profile: {}", activeProfile);
		if (admin == null) {
			User form = new User();
			form.setIsEnabled(true);
			form.setFirstName("bolenum");
			form.setEmailId("admin@bolenum.com");
			if (activeProfile.equals("prod")) {
				form.setPassword(passwordEncoder.encode("M@n!@b0l3num!@#"));
			} else if (activeProfile.equals("stag")) {
				form.setPassword(passwordEncoder.encode("bolenum@oodles"));
			} else {
				form.setPassword(passwordEncoder.encode("12345"));
			}
			form.setRole(roleAdmin);
			User user = userService.saveUser(form);
			etherumWalletService.createEthWallet(user, "ETH");
			String address = btcWalletService.getBtcAccountAddress("");
			userService.saveUserCoin(address, user, "BTC");
			user.setBtcWalletUuid("");
			user.setIsEnabled(true);
			User savedUser = userService.saveUser(user);
			logger.debug("savedUser as Admin: {}", savedUser.getEmailId());
		} else {
			logger.debug("admin exist");
		}
	}

	/**
	 * This method save countries.
	 * @param Nothing.
	 * @return Nothing.
	 */
	void saveCountries() {
		long count = countriesAndStateService.countCountries();
		if (count == 0) {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<Countries>> mapType = new TypeReference<List<Countries>>() {
			};
			InputStream is = TypeReference.class.getResourceAsStream("/json/country.json");
			try {
				List<Countries> countriesList = mapper.readValue(is, mapType);
				countriesAndStateService.saveCountries(countriesList);
				logger.info("Countries list saved successfully");
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.info("Countries list already saved");
		}
	}

	/**
	 * This method save state.
	 * @param Nothing.
	 * @return Nothing.
	 */
	void saveStates() {
		long count = countriesAndStateService.countStates();
		if (count == 0) {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<States>> mapType = new TypeReference<List<States>>() {
			};
			InputStream is = TypeReference.class.getResourceAsStream("/json/state.json");
			try {
				List<States> stateList = mapper.readValue(is, mapType);
				countriesAndStateService.saveStates(stateList);
				logger.info("States list saved successfully");
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.info("States list already saved");
		}
	}

	
	/**
	 * This method save Initial Erc20Tokens.
	 * @param Nothing.
	 * @return Nothing.
	 */
	void saveInitialErc20Tokens() {
		long count = erc20TokenService.countErc20Token();
		if (count == 3) {
			Currency currencyBLN = currencyService
					.saveCurrency(new Currency(currencyName, currencyAbbreviation, CurrencyType.ERC20TOKEN));
			Erc20Token erc20TokenBLN = new Erc20Token(walletAddress, contractAddress, currencyBLN);
			List<Erc20Token> erc20Tokens = new ArrayList<>();
			erc20Tokens.add(erc20TokenBLN);
			erc20TokenService.saveInitialErc20Token(erc20Tokens);
			List<WithdrawalFee> wFee;
			wFee = withdrawalFeeService.getAllWithdrawalFee();
			if (wFee.size() == 2) {
				WithdrawalFee wfBln = new WithdrawalFee();
				wfBln.setCurrency(currencyBLN);
				wfBln.setFee(0.01);
				wfBln.setMinWithDrawAmount(0.01);
				withdrawalFeeService.saveWithdrawalFee(wfBln);
			}
		} else {
			logger.info("Tokens already saved!");
		}
	}

	/**
	 * This method use to add currency.
	 * @param Nothing.
	 * @return Nothing.
	 */
	void saveCurrency() {
		long count = currencyService.countCourencies();
		if (count == 0) {
			Currency bitcoin = new Currency("BITCOIN", "BTC", CurrencyType.CRYPTO);
			Currency ethereum = new Currency("ETHEREUM", "ETH", CurrencyType.CRYPTO);
			Currency ngn = new Currency("NIGERIAN NAIRA", "NGN", CurrencyType.FIAT);
			bitcoin = currencyService.saveCurrency(bitcoin);
			ethereum = currencyService.saveCurrency(ethereum);
			currencyService.saveCurrency(ngn);
			List<WithdrawalFee> wFee;
			wFee = withdrawalFeeService.getAllWithdrawalFee();
			if (wFee.isEmpty()) {
				WithdrawalFee wfBitcoin = new WithdrawalFee();
				wfBitcoin.setCurrency(bitcoin);
				wfBitcoin.setFee(0.001);
				wfBitcoin.setMinWithDrawAmount(0.005);

				WithdrawalFee wfEthereum = new WithdrawalFee();
				wfEthereum.setCurrency(ethereum);
				wfEthereum.setFee(0.01);
				wfEthereum.setMinWithDrawAmount(0.005);

				withdrawalFeeService.saveWithdrawalFee(wfEthereum);
				withdrawalFeeService.saveWithdrawalFee(wfBitcoin);
			}

		}
	}

	/**
	 * This method save initial currency pair.
	 * @param Nothing.
	 * @return Nothing.
	 */
	void saveInitialCurrencyPair() {
		Currency currencyBTC = currencyService.findByCurrencyAbbreviation("BTC");
		Currency currencyETH = currencyService.findByCurrencyAbbreviation("ETH");
		Currency currencyBLN = currencyService.findByCurrencyAbbreviation("BLN");
		Currency currencyNGN = currencyService.findByCurrencyAbbreviation("NGN");
		if (currencyBTC.getMarket().isEmpty()) {
			List<Currency> currencyList = new ArrayList<>();
			currencyList.add(currencyETH);
			currencyList.add(currencyBLN);
			currencyBTC.setMarket(currencyList);
			currencyService.saveCurrency(currencyBTC);
		}
		if (currencyETH.getMarket().isEmpty()) {
			List<Currency> currencyList = new ArrayList<>();
			currencyList.add(currencyBLN);
			currencyETH.setMarket(currencyList);
			currencyService.saveCurrency(currencyETH);
		}
		if (currencyBLN.getMarket().isEmpty()) {
			List<Currency> currencyList = new ArrayList<>();
			currencyList.add(currencyNGN);
			currencyBLN.setMarket(currencyList);
			currencyService.saveCurrency(currencyBLN);
		}
		logger.debug("Currency Pair Saved!");
	}
}
