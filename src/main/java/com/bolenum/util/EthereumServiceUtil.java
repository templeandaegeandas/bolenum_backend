/**
 * 
 */
package com.bolenum.util;

import org.springframework.beans.factory.annotation.Value;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * @author chandan kumar singh
 * @date 26-Sep-2017
 */
public class EthereumServiceUtil implements Cloneable{
	
	@Value("${ethereum.daemon.url}")
	private static String url;
	
	private static Web3j instance = null;

	private EthereumServiceUtil() {

	}
	/**
	 * singleton method for getting instance of ethereum web3j 
	 * @description getWeb3jInstance
	 * @param 
	 * @return Web3j
	 * @exception 
	 *
	 */
	public static Web3j getWeb3jInstance() {
		if (instance == null) {
			HttpService httpService = new HttpService(url);
			instance = Web3j.build(httpService);
			return instance;
		}
		return instance;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
	    throw new CloneNotSupportedException("Clone not supported by EthereumServiceUtil"); 
	}
}
