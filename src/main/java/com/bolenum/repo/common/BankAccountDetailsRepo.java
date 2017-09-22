package com.bolenum.repo.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.BankAccountDetails;

public interface BankAccountDetailsRepo extends JpaRepository<BankAccountDetails, Long> {

	BankAccountDetails findByAccountNumber(String accountNumber);

}
