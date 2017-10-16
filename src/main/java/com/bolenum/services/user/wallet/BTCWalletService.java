/**
 * 
 */
package com.bolenum.services.user.wallet;

import java.util.Map;

/**
 * @author chandan kumar singh
 * @date 22-Sep-2017
 */
public interface BTCWalletService {

	/**
	 * @description createHotWallet
	 * @param 
	 * @return String
	 * @exception 
	 * 
	 */
	String createHotWallet(String uuid);
	
	/**
	 * @description getWalletAddressAndQrCode
	 * @param 
	 * @return List<String>
	 * @exception 
	 *
	 */
	Map<String, Object> getWalletAddressAndQrCode(String uuid);
	
	String getWalletBalnce(String uuid);

	/**
	 * @description getWalletAddress
	 * @param 
	 * @return Map<String,Object>
	 * @exception 
	 * 
	 */
	String getWalletAddress(String walletUuid);
}
