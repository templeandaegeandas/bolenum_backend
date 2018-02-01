/**
 * 
 */
package com.bolenum.services.user.transactions;

import java.util.concurrent.Future;

import org.springframework.data.domain.Page;

import com.bolenum.enums.TransactionStatus;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
public interface TransactionService {

	/**
	 * This method is use to perform Eth Transaction
	 * @param fromUser
	 * @param tokenName
	 * @param toAddress
	 * @param amount
	 * @param fee
	 * @param tradeId
	 * @return
	 */
	Future<Boolean> performEthTransaction(User fromUser, String tokenName, String toAddress, Double amount, Double fee,
			Long tradeId);

	/**
	 * This method is use to perform Btc Transaction
	 * @param fromUser
	 * @param toAddress
	 * @param amount
	 * @param fee
	 * @return
	 */
	public Boolean performBtcTransaction(User fromUser, String toAddress, Double amount, Double fee);

	/**
	 * This method is use to get List Of User Transaction
	 * @param currencyName
	 * @param user
	 * @param withdraw
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @return
	 */
	public Page<Transaction> getListOfUserTransaction(String currencyName, User user, TransactionStatus withdraw,
			int pageNumber, int pageSize, String sortOrder, String sortBy);

	/**
	 * This method is use to perform Erc20 Transaction
	 * @param fromUser
	 * @param tokenName
	 * @param toAddress
	 * @param amount
	 * @param fee
	 * @param tradeId
	 * @return
	 */
	Future<Boolean> performErc20Transaction(User fromUser, String tokenName, String toAddress, Double amount,
			Double fee, Long tradeId);

	/**
	 * This method is use to process Transaction
	 * @param matchedOrder
	 * @param orders
	 * @param qtyTraded
	 * @param buyer
	 * @param seller
	 * @param remainingVolume
	 * @param buyerTradeFee
	 * @param sellerTradeFee
	 * @param trade
	 * @return
	 */
	public Future<Boolean> processTransaction(Orders matchedOrder, Orders orders, double qtyTraded, User buyer,
			User seller, double remainingVolume, double buyerTradeFee, double sellerTradeFee, Trade trade);

	/**
	 * This method is use to fetch Transaction Confirmation
	 * @param page
	 */
	public void fetchTransactionConfirmation(Page<Transaction> page);

	/**
	 * This method is use to withdraw Erc20 Token
	 * @param fromUser
	 * @param tokenName
	 * @param toAddress
	 * @param amount
	 * @param transactionStatus
	 * @param fee
	 * @param tradeId
	 * @return
	 */
	boolean withdrawErc20Token(User fromUser, String tokenName, String toAddress, Double amount,
			TransactionStatus transactionStatus, Double fee, Long tradeId);

	/**
	 * This method is use to withdraw BTC
	 * @param fromUser
	 * @param tokenName
	 * @param toAddress
	 * @param amount
	 * @param fee
	 * @return
	 */
	boolean withdrawBTC(User fromUser, String tokenName, String toAddress, Double amount, Double fee);

	/**
	 * This method is use to withdraw ETH
	 * @param fromUser
	 * @param tokenName
	 * @param toAddress
	 * @param amount
	 * @param fee
	 * @param tradeId
	 * @return
	 */
	boolean withdrawETH(User fromUser, String tokenName, String toAddress, Double amount, Double fee, Long tradeId);

	/**
	 * This method is for total Trasfer Fee Paid By Admin
	 * @param currencyName
	 * @return
	 */
	Double totalTrasferFeePaidByAdmin(String currencyName);

	/**
	 * This method is use to get List Of Transfer Transaction
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @return
	 */
	public Page<Transaction> getListOfTransferTransaction(int pageNumber, int pageSize,
			String sortOrder, String sortBy);
}
