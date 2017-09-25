package com.bolenum.services.common;

import java.util.List;

import com.bolenum.dto.common.EditUserBankDetailsForm;
import com.bolenum.model.BankAccountDetails;
import com.bolenum.model.User;
/**
 * 
 * @Author Himanshu
 * @Date 24-Sep-2017
 */
public interface BankDetailsService {

	public BankAccountDetails findByAccountNumber(String accountNumber);

	public BankAccountDetails saveBankDetails(BankAccountDetails bankAccountDetails);

	public BankAccountDetails updateUserBankDetails(EditUserBankDetailsForm editUserBankDetailsForm,BankAccountDetails isUserBankDetailsExist);

	public List<BankAccountDetails> findByUser(User user);

	public BankAccountDetails findByID(Long id);

}
