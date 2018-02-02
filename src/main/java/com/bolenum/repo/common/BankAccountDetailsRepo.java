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

	/**
	 * This method is use to find BankAccountDetails By Account Number
	 * @param accountNumber
	 * @return
	 */
	public BankAccountDetails findByAccountNumber(String accountNumber);

	/**
	 * This method is use to find List<BankAccountDetails> by User
	 * @param user
	 * @return
	 */
	public List<BankAccountDetails> findByUser(User user);

	/**
	 * This method is use to find BankAccountDetails by id
	 * @param id
	 * @return
	 */
	public BankAccountDetails findById(Long id);

}
