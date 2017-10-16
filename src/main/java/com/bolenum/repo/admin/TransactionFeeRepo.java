package com.bolenum.repo.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.TransactionFee;

public interface TransactionFeeRepo extends JpaRepository<TransactionFee, Long> {
	
}
