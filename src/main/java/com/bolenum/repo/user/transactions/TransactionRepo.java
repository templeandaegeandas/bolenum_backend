/**
 * 
 */
package com.bolenum.repo.user.transactions;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	// TODO check code
	@Query(value = "select SQL_NO_CACHE * from transaction t where t.tx_hash=:txHash", nativeQuery = true)
	Transaction findByTransactionHash(@Param("txHash") String txHash);

	Page<Transaction> findByFromUserAndTransactionStatus(User fromUser, TransactionStatus transactionStatus,
			Pageable pageable);

	@Query("select t from Transaction t where t.toUser=:toUser and (t.transactionStatus='WITHDRAW' or t.transactionStatus='DEPOSIT')")
	Page<Transaction> findByToUserAndTransactionStatusOrTransactionStatus(@Param("toUser") User toUser,
			Pageable pageable);

}
