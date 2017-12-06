package com.bolenum.repo.order.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.enums.DisputeStatus;
import com.bolenum.model.orders.book.DisputeOrder;
import com.bolenum.model.orders.book.Orders;

public interface DisputeOrderRepo extends JpaRepository<DisputeOrder, Long> {

	DisputeOrder findByOrdersOrTransactionId(Orders order, Long transactionId);

	Page<DisputeOrder> findByDisputeStatus(DisputeStatus disputeStatus, Pageable pageRequest);

}
