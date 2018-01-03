package com.bolenum.repo.order.book;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bolenum.model.CurrencyPair;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Trade;

public interface TradeRepository extends JpaRepository<Trade, Long> {

	@Query("select t from Trade t where (t.buyer =:buyer or t.seller =:seller) and t.createdOn > :startDate and t.createdOn < :endDate")
	Page<Trade> getByBuyerOrSellerWithDate(@Param("buyer") User buyer, @Param("seller") User seller,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

	Page<Trade> findByBuyerOrSeller(User buyer, User seller, Pageable pageable);

	@Query("select t from Trade t where t.buyer =:buyer and t.createdOn > :startDate and t.createdOn < :endDate")
	Page<Trade> getByBuyerWithDate(@Param("buyer") User buyer, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate, Pageable pageable);

	Page<Trade> findByBuyer(User buyer, Pageable pageable);

	@Query("select t from Trade t where t.seller =:seller and t.createdOn > :startDate and t.createdOn < :endDate")
	Page<Trade> getBySellerWithDate(@Param("seller") User seller, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate, Pageable pageable);

	Page<Trade> findBySeller(User seller, Pageable pageable);

	@Query("select count(t) from Trade t where t.pair=:pair and t.createdOn > :endDate")
	long count24hTrade(@Param("pair") CurrencyPair pair, @Param("endDate") Date endDate);
}
