/**
 * 
 */
package com.bolenum.services.user.wallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 17-Oct-2017
 */
@Service
public class WalletServiceImpl implements WalletService {

	private Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

	@Autowired
	private BTCWalletService btcWalletService;

	@Autowired
	private EtherumWalletService etherumWalletService;

	@Override
	public String getBalance(String ticker, User user) {
		logger.debug("get wallet balance ticker: {}", ticker);
		String balance = null;
		switch (ticker) {
		case "BTC":
			balance = btcWalletService.getWalletBalnce(user.getBtcWalletUuid());
			break;
		case "ETH":
			balance = String.valueOf(etherumWalletService.getWalletBalance(user));
			break;
		case "ERC20TOKEN":
			break;
		case "FIAT":
			break;
		}
		logger.debug("get wallet balance: {}", balance);
		return balance;
	}

}
