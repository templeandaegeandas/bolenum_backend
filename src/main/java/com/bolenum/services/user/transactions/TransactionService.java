/**
 * 
 */
package com.bolenum.services.user.transactions;

import com.bolenum.enums.TransactionStatus;


import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;

import com.bolenum.model.Transaction;
import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
public interface TransactionService {
	public boolean performEthTransaction(User fromUser, String toAddress, Double amount,TransactionStatus transactionStatus);
	public boolean performBtcTransaction(User fromUser, String toAddress, Double amount,TransactionStatus transactionStatus);
	
	@Async
	public boolean performTransaction(String currencyAbr, double qtyTraded, User buyer, User seller);
	public Page<Transaction> getListOfUserTransaction(User user, TransactionStatus withdraw, int pageNumber, int pageSize,
			String sortOrder, String sortBy);
}
