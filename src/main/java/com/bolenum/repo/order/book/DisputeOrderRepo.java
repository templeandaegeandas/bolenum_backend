package com.bolenum.repo.order.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.enums.DisputeStatus;
import com.bolenum.model.orders.book.DisputeOrder;

public interface DisputeOrderRepo extends JpaRepository<DisputeOrder, Long> {

	DisputeOrder findByOrderIdOrTransactionId(Long orderId, Long transactionId);

	Page<DisputeOrder> findByDisputeStatus(DisputeStatus disputeStatus, Pageable pageRequest);

}
