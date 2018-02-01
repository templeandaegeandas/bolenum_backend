/**
 * 
 */
package com.bolenum.repo.user.transactions;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
	 * This method is use to find Transaction by Tx Hash
	 * @description findByTxHash
	 * 
	 */
	Transaction findByTxHash(String txHash);

	/**
	 * This method is use to find Transaction by From User And TransactionStatus And CurrencyName
	 * @param fromUser
	 * @param transactionStatus
	 * @param currencyName
	 * @param pageable
	 * @return
	 */
	Page<Transaction> findByFromUserAndTransactionStatusAndCurrencyName(User fromUser,
			TransactionStatus transactionStatus, String currencyName, Pageable pageable);

	/**
	 * This method is use to find Transaction By ToUser And CurrencyName And TransactionStatus Or TransactionStatus
	 * @param toUser
	 * @param currencyName
	 * @param pageable
	 * @return
	 */
	@Query("select t from Transaction t where t.toUser=:toUser and t.currencyName=:currencyName and (t.transactionStatus='WITHDRAW' or t.transactionStatus='DEPOSIT')")
	Page<Transaction> findByToUserAndCurrencyNameAndTransactionStatusOrTransactionStatus(@Param("toUser") User toUser,
			@Param("currencyName") String currencyName, Pageable pageable);

	/**
	 * This method is use to find transaction by TransactionStatus And TransferStatus NotIn OrderBy CreatedOn Asc
	 * @param transactionStatus
	 * @param transferStatus
	 * @return
	 */
	Transaction findFirstByTransactionStatusAndTransferStatusOrderByCreatedOnAsc(TransactionStatus transactionStatus,
			TransferStatus transferStatus);

	/**
	 * This method is use to find transaction by ToUser And CurrencyName And TransactionStatus And TransferStatus NotIn
	 * @param toUser
	 * @param currencyName
	 * @param transactionStatus
	 * @param transferStatus
	 * @return
	 */
	List<Transaction> findByToUserAndCurrencyNameAndTransactionStatusAndTransferStatus(User toUser, String currencyName,
			TransactionStatus transactionStatus, TransferStatus transferStatus);

	/**
	 * This method is use for total TrasferFee PaidBy Admin
	 * @param currencyName
	 * @return
	 */
	@Query("select sum(t.fee) from Transaction t where t.transactionStatus='TRANSFER' and t.currencyName =:currencyName")
	Double totalTrasferFeePaidByAdmin(@Param("currencyName") String currencyName);

	/**
	 * tgis method is use to find transaction by TransactionStatus
	 * @param transactionStatus
	 * @param pageable
	 * @return
	 */
	Page<Transaction> findByTransactionStatus(TransactionStatus transactionStatus, Pageable pageable);

}
