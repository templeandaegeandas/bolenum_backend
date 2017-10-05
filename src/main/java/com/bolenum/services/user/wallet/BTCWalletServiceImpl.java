/**
 * 
 */
package com.bolenum.services.user.wallet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.bolenum.constant.BTCUrlConstant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author chandan kumar singh
 * @date 22-Sep-2017
 */
@Service
public class BTCWalletServiceImpl implements BTCWalletService {

	private static final Logger logger = LoggerFactory.getLogger(BTCWalletServiceImpl.class);

	/**
	 * creating BIP32 hierarchical deterministic (HD) wallets
	 */
	@Override
	public String createHotWallet(String uuid) {
		String url = BTCUrlConstant.HOT_WALLET;
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<String, String>();
		logger.debug("create wallet uuid:  {}", uuid);
		parametersMap.add("uuid", uuid);
		Map<String, Object> map = new HashMap<String, Object>();
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
			e.printStackTrace();
		} catch (JsonParseException e) {
			logger.error("create wallet exception JPE:  {}", e.getMessage());
			e.printStackTrace();
		} catch (JsonMappingException e) {
			logger.error("create wallet exception JME:  {}", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("create wallet exception IOE:  {}", e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getWalletAddressAndQrCode(String walletUuid) {
		String url = BTCUrlConstant.WALLET_ADDR;
		RestTemplate restTemplate = new RestTemplate();
		logger.debug("get Wallet Address And QrCode uuid:  {}", walletUuid);
		Map<String, Object> map = new HashMap<String, Object>();
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("walletUuid", walletUuid);
		try {
			Map<String, Object> res = restTemplate.getForObject(builder.toUriString(), Map.class);
			logger.debug("get Wallet Address And QrCode res map: {}", res);
			boolean isError = (boolean) res.get("error");
			if (!isError) {
				String bal = getWalletBalnce(walletUuid);
				Map<String,Object> data= (Map<String,Object>) res.get("data");
				res.clear();
				res.put("address", data.get("address"));
				res.put("file_name", data.get("file_name"));
				res.put("balance", bal);
				map.put("data", res);
			}
		} catch (RestClientException e) {
			logger.error("get Wallet Address And QrCode exception RCE:  {}", e.getMessage());
			e.printStackTrace();
		}
		return map;
	}
	@SuppressWarnings("unchecked")
	@Override
	public String getWalletBalnce(String uuid) {
		String url = BTCUrlConstant.WALLET_BAL;
		RestTemplate restTemplate = new RestTemplate();
		logger.debug("get Wallet balance:  {}", uuid);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("walletUuid", uuid);
		try {
			Map<String, Object> res = restTemplate.getForObject(builder.toUriString(), Map.class);
			return (String) res.get("data");
		} catch (RestClientException e) {
			logger.error("get Wallet balance RCE:  {}", e.getMessage());
			e.printStackTrace();
		}
		return "";
	}
}
