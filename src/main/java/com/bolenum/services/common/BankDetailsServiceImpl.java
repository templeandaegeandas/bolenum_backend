package com.bolenum.services.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.bolenum.model.BankAccountDetails;
import com.bolenum.repo.common.BankAccountDetailsRepo;

/**
 * 
 * @Author himanshu
 * @Date 22-Sep-2017
 */


public class BankDetailsServiceImpl implements BankDetailsService {

	@Autowired
	private BankAccountDetailsRepo bankAccountDetailsRepo; 
	
	@Override
	public BankAccountDetails findByAccountNumber(String accountNumber) {
		
		return bankAccountDetailsRepo.findByAccountNumber(accountNumber);
	}


	@Override
	public BankAccountDetails saveBankDetails(BankAccountDetails bankAccountDetails) {
		
		return null;
	}

}
