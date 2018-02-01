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
	/**
	 * This method is use for perform Trade Transaction
	 * @param tfee
	 * @param currencyAbr
	 * @param currencyType
	 * @param qtyTraded
	 * @param buyer
	 * @param seller
	 * @param tradeId
	 * @return
	 */
	public Boolean performTradeTransaction(double tfee, String currencyAbr, String currencyType, double qtyTraded, User buyer,
			User seller, Long tradeId);
	
	/**
	 * This method is use for perform Trade Transaction Fee
	 * @param currencyAbr
	 * @param currencyType
	 * @param tradeFee
	 * @param buyer
	 * @param seller
	 * @param tradeId
	 * @return
	 */
	public Boolean performTradeTransactionFee(String currencyAbr, String currencyType, double tradeFee, User buyer,
			User seller, Long tradeId);

	/**
	 * This method is use to perform Erc20 Trade
	 * @param seller
	 * @param currencyAbr
	 * @param buyer
	 * @param qtyTraded
	 * @param tradeId
	 * @return
	 */
	public Boolean performErc20Trade(User seller, String currencyAbr, User buyer, double qtyTraded, Long tradeId);

	/**
	 * This method is use to perform Eth Trade
	 * @param seller
	 * @param currencyAbr
	 * @param buyer
	 * @param qtyTraded
	 * @param tradeId
	 * @return
	 */
	public Boolean performEthTrade(User seller, String currencyAbr, User buyer, double qtyTraded, Long tradeId);

	/**
	 * This method is use to perform Btc Trade
	 * @param fromUser
	 * @param toUser
	 * @param qtyTraded
	 * @param tradeId
	 * @return
	 */
	public Boolean performBtcTrade(User fromUser, User toUser, double qtyTraded, Long tradeId);
}
