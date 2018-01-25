/**
 * 
 */
package com.bolenum.services.user.trade;

import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 19-Dec-2017
 */
public interface TradeTransactionService {
	public Boolean performTradeTransaction(double tfee, String currencyAbr, String currencyType, double qtyTraded, User buyer,
			User seller, Long tradeId);
	public Boolean performTradeTransactionFee(String currencyAbr, String currencyType, double tradeFee, User buyer,
			User seller, Long tradeId);

	public Boolean performErc20Trade(User seller, String currencyAbr, User buyer, double qtyTraded, Long tradeId);

	public Boolean performEthTrade(User seller, String currencyAbr, User buyer, double qtyTraded, Long tradeId);

	public Boolean performBtcTrade(User fromUser, User toUser, double qtyTraded, Long tradeId);
}
