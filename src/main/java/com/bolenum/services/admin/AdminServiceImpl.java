package com.bolenum.services.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.bolenum.constant.UrlConstant;
import com.bolenum.model.User;
import com.bolenum.model.erc20token.Erc20Token;
import com.bolenum.repo.common.erc20token.Erc20TokenRepository;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.services.user.wallet.BTCWalletService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author vishal_kumar
 * @date 15-sep-2017
 *
 */

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BTCWalletService btcWalletService;

	@Autowired
	private Erc20TokenRepository erc20TokenRepository;

	@Value("${bitcoin.service.url}")
	private String btcUrl;

	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	/**
	 * 
	 */
	@Override
	public Page<User> getUsersList(int pageNumber, int pageSize, String sortBy, String sortOrder, String searchData,
			User user) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return userRepository.getUserListWithSearch(searchData, user.getUserId(), pageRequest);
	}

	/**
	 * 	
	 */
	@Override
	public User getUserById(Long userId) {
		return userRepository.findOne(userId);
	}

	/**
	 * 
	 */
	@Override
	public String createAdminHotWallet(String uuid) {
		String url = btcUrl + UrlConstant.ADMIN_HOT_WALLET;
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<>();
		logger.debug("create wallet uuid:  {}", uuid);
		parametersMap.add("uuid", uuid);
		Map<String, Object> map = new HashMap<>();
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
		} catch (JsonParseException e) {
			logger.error("create wallet exception JPE:  {}", e.getMessage());
		} catch (JsonMappingException e) {
			logger.error("create wallet exception JME:  {}", e.getMessage());
		} catch (IOException e) {
			logger.error("create wallet exception IOE:  {}", e.getMessage());
		}
		return "";

	}

	@SuppressWarnings("unchecked")
	@Override
	public String getAdminWalletBalnce(String uuid) {
		String url = btcUrl + UrlConstant.WALLET_BAL;
		RestTemplate restTemplate = new RestTemplate();
		logger.debug("get Wallet balance:  {}", uuid);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("walletUuid", uuid);
		try {
			Map<String, Object> res = restTemplate.getForObject(builder.toUriString(), Map.class);
			return (String) res.get("data");
		} catch (RestClientException e) {
			logger.error("get Wallet balance RCE:  {}", e.getMessage());
		}
		return "";
	}

	@Override
	public boolean adminWithdrawCryptoAmount(User user, String tokenName, Double withdrawAmount, String toAddress) {
		return btcWalletService.adminWithdrawCryptoAmount(user, tokenName, withdrawAmount, toAddress);
	}

	@Override
	public Future<Boolean> adminWithdrawErc20TokenAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		return btcWalletService.adminWithdrawErc20TokenAmount(user, tokenName, withdrawAmount, toAddress);
	}

	@Override
	public boolean adminValidateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress) {
		Erc20Token erc20Token = erc20TokenRepository.findByCurrencyCurrencyAbbreviation(tokenName);
		return btcWalletService.adminValidateErc20WithdrawAmount(user, tokenName, withdrawAmount, toAddress, erc20Token);
	}

	@Override
	public boolean adminValidateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress) {
		return btcWalletService.adminValidateCryptoWithdrawAmount(user, tokenName, withdrawAmount, toAddress);
	}
}
