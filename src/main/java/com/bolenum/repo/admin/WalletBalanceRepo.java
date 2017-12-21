package com.bolenum.repo.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.coin.WalletBalance;

public interface WalletBalanceRepo extends JpaRepository<WalletBalance,Long> {

	
}
