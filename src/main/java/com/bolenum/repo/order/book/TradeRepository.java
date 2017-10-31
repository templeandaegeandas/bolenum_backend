package com.bolenum.repo.order.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bolenum.model.User;
import com.bolenum.model.orders.book.Trade;

public interface TradeRepository extends JpaRepository<Trade, Long>{

	@Query("select t from Trade t where t.buyer=:buyer or t.seller=:seller")
	Page<Trade> findByBuyerOrSeller(@Param("buyer")User buyer, @Param("seller")User Seller, Pageable pageable);
}
