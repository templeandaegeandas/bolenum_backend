package com.bolenum.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.bolenum.enums.CurrencyType;
import com.bolenum.model.Countries;
import com.bolenum.model.Currency;
import com.bolenum.model.Erc20Token;
import com.bolenum.model.Privilege;
import com.bolenum.model.Role;
import com.bolenum.model.States;
import com.bolenum.model.User;
import com.bolenum.services.admin.AdminService;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.admin.Erc20TokenService;
import com.bolenum.services.common.CountryAndStateService;
import com.bolenum.services.common.PrivilegeService;
import com.bolenum.services.common.RoleService;
import com.bolenum.services.user.UserService;
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
	private AdminService adminService;

	@Autowired
	private EtherumWalletService etherumWalletService;

	private Set<Privilege> privileges = new HashSet<>();

	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		addPrivileges();
		addRole();
		createAdmin();
		saveCountries();
		saveStates();

		saveInitialErc20Tokens();
		saveCurrency();

		// create initial directories
		createInitDirectories();
		createProfilePicDirectories();
		createDocumentsDirectories();
		createGoogleAuthQrCodeDirectories();

	}

	/**
	 * this will create ethereum wallet location at the time of application
	 * start @description createInitDirectories @param @return void @exception
	 * 
	 */

	private void createDocumentsDirectories() {
		Path profileImg = Paths.get(userDocumetsLocation);

		if (!Files.exists(profileImg)) {
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
	 * 
	 */
	private void createGoogleAuthQrCodeDirectories() {
		Path profileImg = Paths.get(googleQrCodeLocation);

		if (!Files.exists(profileImg)) {
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
	 * this will create ethereum wallet location at the time of application
	 * start @description createInitDirectories @param @return void @exception
	 * 
	 */
	private void createInitDirectories() {
		Path ethWallet = Paths.get(ethWalletLocation);
		if (!Files.exists(ethWallet)) {
			if (new File((ethWalletLocation)).mkdirs()) {
				logger.debug("ethereum wallet location created");
			} else {
				logger.debug("ethereum wallet location creation failed");
			}
		} else {
			logger.debug("ethereum wallet location exists");
		}

	}

	private void createProfilePicDirectories() {
		Path profileImg = Paths.get(userProfileImageLocation);

		if (!Files.exists(profileImg)) {
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
	 * @description addRole @param @return void @exception
	 */
	private void addRole() {
		Role role = new Role("ROLE_USER", "user role", privilegeService.findAllPrevileges());
		roleService.findOrCreate(role);
	}

	/**
	 * @description addPrivileges @param @return void @exception
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
		privileges.forEach(Privilege -> privilegeService.findOrCreate(Privilege));
	}

	/**
	 * @description createAdmin @param @return void @exception
	 */
	private void createAdmin() {
		Role r = new Role("ROLE_ADMIN", "Admin role", privilegeService.findAllPrevileges());
		Role roleAdmin = roleService.findOrCreate(r);
		User admin = userService.findByEmail("admin@bolenum.com");
		if (admin == null) {
			User form = new User();
			form.setIsEnabled(true);
			form.setFirstName("bolenum");
			form.setEmailId("admin@bolenum.com");
			form.setPassword(passwordEncoder.encode("12345"));
			form.setRole(roleAdmin);
			User user = userService.saveUser(form);
			etherumWalletService.createWallet(user);
			String uuid = adminService.createAdminHotWallet("adminWallet");
			logger.debug("user mail verify wallet uuid: {}", uuid);
			if (!uuid.isEmpty()) {
				user.setBtcWalletUuid(uuid);
				user.setIsEnabled(true);
				User savedUser = userService.saveUser(user);
				logger.debug("savedUser as Admin: {}", savedUser.getEmailId());
			} else {
				logger.debug("admin exist");
			}
		}
	}

	/**
	 * 
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
	 * 
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

	void saveInitialErc20Tokens() {
		long count = erc20TokenService.countErc20Token();
		if (count == 2) {
			Currency currencyBLN = currencyService
					.saveCurrency(new Currency(currencyName, currencyAbbreviation, CurrencyType.ERC20TOKEN));
			Erc20Token erc20TokenBLN = new Erc20Token(walletAddress, contractAddress, currencyBLN);
			List<Erc20Token> erc20Tokens = new ArrayList<>();
			erc20Tokens.add(erc20TokenBLN);
			erc20TokenService.saveInitialErc20Token(erc20Tokens);

		}
		else {
			logger.info("Tokens already saved!");
		}
	}

	/**
	 * to add currency
	 */
	void saveCurrency() {
		long count = currencyService.countCourencies();
		if (count == 0) {
			Currency currency1 = new Currency("BITCOIN", "BTC", CurrencyType.CRYPTO);
			Currency currency2 = new Currency("ETHEREUM", "ETH", CurrencyType.CRYPTO);
			currencyService.saveCurrency(currency1);
			currencyService.saveCurrency(currency2);
		}
	}
}
