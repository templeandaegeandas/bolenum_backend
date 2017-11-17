package com.bolenum.services.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.dto.common.EditUserBankDetailsForm;
import com.bolenum.model.BankAccountDetails;
import com.bolenum.model.User;
import com.bolenum.repo.common.BankAccountDetailsRepo;

/**
 * 
 * @Author himanshu
 * @Date 22-Sep-2017
 * @updated Chandan Kumar Singh
 */

@Service
public class BankAccountDetailsServiceImpl implements BankAccountDetailsService {

	@Autowired
	private BankAccountDetailsRepo bankAccountDetailsRepo;

	/**
	 * to find bank account details with respect to account number
	 */
	@Override
	public BankAccountDetails findByAccountNumber(String accountNumber) {

		return bankAccountDetailsRepo.findByAccountNumber(accountNumber);
	}

	/**
	 * to save bank Account details
	 */
	@Override
	public BankAccountDetails saveBankDetails(BankAccountDetails bankAccountDetails) {

		return bankAccountDetailsRepo.saveAndFlush(bankAccountDetails);
	}

	/**
	 * to update user bank details
	 */
	@Override
	public BankAccountDetails updateUserBankDetails(EditUserBankDetailsForm editUserBankDetailsForm,
			BankAccountDetails bankAccountDetails) {

		if (editUserBankDetailsForm.getAccountHolderName() != null) {
			bankAccountDetails.setAccountHolderName(editUserBankDetailsForm.getAccountHolderName());
		}
		if (editUserBankDetailsForm.getAddress() != null) {
			bankAccountDetails.setAddress(editUserBankDetailsForm.getAddress());
		}

		if (editUserBankDetailsForm.getAccountNumber() != null) {
			bankAccountDetails.setAccountNumber(editUserBankDetailsForm.getAccountNumber());
		}

		if (editUserBankDetailsForm.getBankName() != null) {
			bankAccountDetails.setBankName(editUserBankDetailsForm.getBankName());
		}

		if (editUserBankDetailsForm.getBranch() != null) {
			bankAccountDetails.setBranch(editUserBankDetailsForm.getBranch());
		}

		if (editUserBankDetailsForm.getCity() != null) {
			bankAccountDetails.setCity(editUserBankDetailsForm.getCity());
		}

		if (editUserBankDetailsForm.getContactNumber() != null) {
			bankAccountDetails.setContactNumber(editUserBankDetailsForm.getContactNumber());
		}

		return bankAccountDetailsRepo.saveAndFlush(bankAccountDetails);
	}

	/**
	 * 
	 * returns BankAccountDetails with respect to specific user
	 * 
	 */
	@Override
	public List<BankAccountDetails> findByUser(User user) {
		List<BankAccountDetails> listOfBankAccountDetails = bankAccountDetailsRepo.findByUser(user);
		return listOfBankAccountDetails;
	}

	/**
	 * returns BankAccountDetails with respect to specific user id
	 */
	@Override
	public BankAccountDetails findByID(Long id) {
		return bankAccountDetailsRepo.findById(id);
	}

	@Override
	public BankAccountDetails primaryBankAccountDetails(User user) {
		List<BankAccountDetails> bankAccountDetails = bankAccountDetailsRepo.findByUser(user);
		BankAccountDetails bankAccountDetail = null;
		for (BankAccountDetails bank : bankAccountDetails) {
			if (bank.isPrimary()) {
				bankAccountDetail = bank;
				break;
			}
		}
		if (bankAccountDetail == null) {
			bankAccountDetail = bankAccountDetails.get(0);
		}
		return bankAccountDetail;
	}

	@Override
	public boolean isBankAccountAdded(User user) {
		List<BankAccountDetails> bankAccountDetails = bankAccountDetailsRepo.findByUser(user);
		if (bankAccountDetails.size() > 0) {
			return true;
		}
		return false;
	}

}
