/**
 * 
 */
package com.bolenum.services.user;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author chandan kumar singh
 * @date 22-Sep-2017
 */
@Service
public class BTCWalletServiceImpl implements BTCWalletService{

	@Override
	public JSONObject createHotWallet(String uuid) {
		String url = "http://127.0.0.1/api/v1/hotwallet/create";
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> param = new HashMap<>();
		param.put("uuid", uuid);
		JSONObject json = restTemplate.postForObject(url, null, JSONObject.class, uuid);
		return json;
	}
}
