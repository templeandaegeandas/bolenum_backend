/**
 * 
 */
package com.bolenum.services.user.transactions;

import com.bolenum.enums.TransactionStatus;
import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
public interface TransactionService {
	public boolean performEthTransaction(User fromUser, String toAddress, Double amount,TransactionStatus transactionStatus);
	public boolean performBtcTransaction(User fromUser, String toAddress, Double amount,TransactionStatus transactionStatus);
}
