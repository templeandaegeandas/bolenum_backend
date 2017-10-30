package com.bolenum.repo.order.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.orders.book.Trade;

public interface TradeRepository extends JpaRepository<Trade, Long>{

	Page<Trade> findByBuyerIdOrSellerId(Long buyesrId, Long SellerId, Pageable pageable);
}
