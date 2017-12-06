/**
 * 
 */
package com.bolenum.util;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * @author chandan kumar singh
 * @date 26-Sep-2017
 */
public class EthereumServiceUtil implements Cloneable{
	
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
			HttpService httpService = new HttpService("http://10.136.16.90:8000");
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
