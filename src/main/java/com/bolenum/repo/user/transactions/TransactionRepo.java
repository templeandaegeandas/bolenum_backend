/**
 * 
 */
package com.bolenum.repo.user.transactions;

import java.io.Serializable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.enums.TransactionStatus;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
public interface TransactionRepo extends JpaRepository<Transaction, Serializable> {

	/**
	 * @description findByTxHash 
	 * 
	 */
	Transaction findByTxHash(String txHash);
	
	Page<Transaction> findByUserAndTransactionStatus(User user,TransactionStatus transactionStatus, Pageable pageable);

}
