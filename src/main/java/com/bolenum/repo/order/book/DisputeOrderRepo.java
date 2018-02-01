package com.bolenum.repo.order.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.enums.DisputeStatus;
import com.bolenum.model.orders.book.DisputeOrder;
import com.bolenum.model.orders.book.Orders;

public interface DisputeOrderRepo extends JpaRepository<DisputeOrder, Long> {

	/**
	 * This method is use to find By Orders Or TransactionId
	 * @param order
	 * @param transactionId
	 * @return
	 */
	DisputeOrder findByOrdersOrTransactionId(Orders order, Long transactionId);

	/**
	 * This method is use to find By Dispute Status
	 * @param disputeStatus
	 * @param pageRequest
	 * @return
	 */
	Page<DisputeOrder> findByDisputeStatus(DisputeStatus disputeStatus, Pageable pageRequest);

}
