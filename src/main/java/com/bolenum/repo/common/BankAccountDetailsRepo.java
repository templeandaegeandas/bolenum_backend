package com.bolenum.repo.common;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.BankAccountDetails;
import com.bolenum.model.User;
/**
 * 
 * @Author Himanshu
 * @Date 24-Sep-2017
 */
public interface BankAccountDetailsRepo extends JpaRepository<BankAccountDetails, Long> {

	public BankAccountDetails findByAccountNumber(String accountNumber);

	public List<BankAccountDetails> findByUser(User user);

	public BankAccountDetails findById(Long id);

}
