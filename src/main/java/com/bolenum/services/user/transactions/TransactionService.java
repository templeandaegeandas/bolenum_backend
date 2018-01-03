/**
 * 
 */
package com.bolenum.services.user.transactions;

import java.util.concurrent.ExecutionException;
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

	Future<Boolean> performEthTransaction(User fromUser, String tokenName, String toAddress, Double amount, Double fee,
			Long tradeId);

	public Future<Boolean> performBtcTransaction(User fromUser, String toAddress, Double amount,
			TransactionStatus transactionStatus, Double fee, Long tradeId);

	public Boolean performBtcTransaction(User fromUser, String toAddress, Double amount, Double fee);

	public Future<Boolean> performTransaction(String currencyAbr, double qtyTraded, User buyer, User seller,
			boolean isFee, Long tradeID) throws InterruptedException, ExecutionException;

	public Page<Transaction> getListOfUserTransaction(String currencyName, User user, TransactionStatus withdraw, int pageNumber,
			int pageSize, String sortOrder, String sortBy);

	Future<Boolean> performErc20Transaction(User fromUser, String tokenName, String toAddress, Double amount,
			Double fee, Long tradeId);

	public Future<Boolean> processTransaction(Orders matchedOrder, Orders orders, double qtyTraded, User buyer,
			User seller, double remainingVolume, double buyerTradeFee, double sellerTradeFee, Trade trade)
			throws InterruptedException, ExecutionException;

	public void fetchTransactionConfirmation(Page<Transaction> page);

	boolean withdrawErc20Token(User fromUser, String tokenName, String toAddress, Double amount,
			TransactionStatus transactionStatus, Double fee, Long tradeId);

	boolean withdrawBTC(User fromUser, String tokenName, String toAddress, Double amount, Double fee);

	boolean withdrawETH(User fromUser, String tokenName, String toAddress, Double amount,
			Double fee, Long tradeId);
}
