/**
 * 
 */
package com.bolenum.services.user.transactions;

import org.springframework.scheduling.annotation.Async;

import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
public interface TransactionService {
	public boolean performEthTransaction(User fromUser, String toAddress, Double amount);
	public boolean performBtcTransaction(User fromUser, String toAddress, Double amount);
	@Async
	public boolean performTransaction(String currencyAbr, double qtyTraded, User buyer, User seller);
}
