/**
 * 
 */
package com.bolenum.repo.user.transactions;

import java.io.Serializable;
import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bolenum.enums.TransactionStatus;
import com.bolenum.enums.TransferStatus;
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

	Page<Transaction> findByFromUserAndTransactionStatusAndCurrencyName(User fromUser,
			TransactionStatus transactionStatus, String currencyName, Pageable pageable);

	@Query("select t from Transaction t where t.toUser=:toUser and t.currencyName=:currencyName and (t.transactionStatus='WITHDRAW' or t.transactionStatus='DEPOSIT')")
	Page<Transaction> findByToUserAndCurrencyNameAndTransactionStatusOrTransactionStatus(@Param("toUser") User toUser,
			@Param("currencyName") String currencyName, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Transaction findFirstByTransactionStatusAndTransferStatusNotInOrderByCreatedOnAsc(TransactionStatus transactionStatus,
			TransferStatus transferStatus);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<Transaction> findByToUserAndCurrencyNameAndTransactionStatusAndTransferStatusNotIn(User toUser, String currencyName,
			TransactionStatus transactionStatus, TransferStatus transferStatus);

	@Query("select sum(t.fee) from Transaction t where t.transactionStatus='TRANSFER' and t.currencyName =:currencyName")
	Double totalTrasferFeePaidByAdmin(@Param("currencyName") String currencyName);

	Page<Transaction> findByTransactionStatus(TransactionStatus transactionStatus, Pageable pageable);

}
