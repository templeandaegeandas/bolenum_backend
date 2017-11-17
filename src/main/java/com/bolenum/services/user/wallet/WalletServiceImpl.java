/**
 * 
 */
package com.bolenum.services.user.wallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.Erc20Token;
import com.bolenum.model.User;
import com.bolenum.services.admin.Erc20TokenService;

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
	
	@Autowired
	private Erc20TokenService erc20TokenService;

	@Override
	public String getBalance(String ticker, String currencyType, User user) {
		logger.debug("get wallet balance ticker: {}", ticker);
		String balance = null;
		switch(currencyType) {
		case "CRYPTO":
			switch(ticker) {
			case "BTC":
				balance = btcWalletService.getWalletBalnce(user.getBtcWalletUuid());
				break;
			case "ETH":
				balance = String.valueOf(etherumWalletService.getWalletBalance(user));
				break;
			}
			break;
		case "ERC20TOKEN":
			Erc20Token erc20Token = erc20TokenService.getByCoin(ticker);
			balance = String.valueOf(erc20TokenService.getErc20WalletBalance(user, erc20Token));
			break;
		case "FIAT":
			break;
		}
		logger.debug("get wallet balance: {}", balance);
		return balance;
	}

}