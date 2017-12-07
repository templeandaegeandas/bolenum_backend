/**
 * 
 */
package com.bolenum.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * @author chandan kumar singh
 * @date 26-Sep-2017
 */
@Component
public class EthereumServiceUtil {

	@Value("${ethereum.service.url}")
	private String url;

	private static Web3j instance = null;

	@PostConstruct
	void init() {
		HttpService httpService = new HttpService(url);
		instance = Web3j.build(httpService);
	}

	private EthereumServiceUtil() {

	}

	/**
	 * method for getting instance of ethereum web3j
	 */
	public static Web3j getWeb3jInstance() {
		return instance;
	}
}
