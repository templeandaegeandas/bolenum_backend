package com.bolenum.repo.order.book;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.orders.book.Trade;

public interface TradeRepository extends JpaRepository<Trade, Long>{

}
