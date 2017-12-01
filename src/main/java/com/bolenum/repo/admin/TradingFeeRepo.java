package com.bolenum.repo.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.fees.TradingFee;

public interface TradingFeeRepo extends JpaRepository<TradingFee, Long> {
	
}
