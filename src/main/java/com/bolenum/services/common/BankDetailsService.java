package com.bolenum.services.common;

import com.bolenum.model.BankAccountDetails;


public interface BankDetailsService {

	public BankAccountDetails findByAccountNumber(String accountNumber);

	public BankAccountDetails saveBankDetails(BankAccountDetails bankAccountDetails);

}
