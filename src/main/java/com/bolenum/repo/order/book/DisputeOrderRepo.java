package com.bolenum.repo.order.book;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.orders.book.DisputeOrder;

public interface DisputeOrderRepo extends JpaRepository<DisputeOrder, Long> {

	DisputeOrder findByOrderIdAndTransactionId(Long orderId, Long transactionId);

}
