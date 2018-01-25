/*@Description Of class
 * 
 * BankAccountDetailsServiceImpl interface is responsible for below listed task: 
 * 
 * 		Bank details find by account number
 * 		Save bank details
 *      Update user bank details
 *      Find user
 *      Find user by id
 *      Primary bank account details
 *      Check is bank account added
 */

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

	/**@description use to get bank account details by using account number
	 * @param       accountNumber
	 * @return      bank account details
	 */
	@Override
	public BankAccountDetails findByAccountNumber(String accountNumber) {

		return bankAccountDetailsRepo.findByAccountNumber(accountNumber);
	}

	/**@description use to save bank account details
	 * @param       bank account details
	 * @return      bank account details
	 */
	@Override
	public BankAccountDetails saveBankDetails(BankAccountDetails bankAccountDetails) {

		return bankAccountDetailsRepo.saveAndFlush(bankAccountDetails);
	}

	/**@description use to update bank account details
	 * @param       editUserBankDetailsForm          
	 * @param       isUserBankDetailsExist
	 * @return      bank account details
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

	/**@description use to find bank account details by user
	 * @param       user
	 * @return      list of bank account details
	 */
	@Override
	public List<BankAccountDetails> findByUser(User user) {
		return bankAccountDetailsRepo.findByUser(user);
	}

	/**@description use to find bank account details by ID
	 * @param       id
	 * @return      list of bank account details
	 */
	@Override
	public BankAccountDetails findByID(Long id) {
		return bankAccountDetailsRepo.findById(id);
	}
	
	
	/**@description use to find primary bank account details by user
	 * @param       user
	 * @return      list of bank account details
	 */
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
	/**@description use to check bank account added or not
	 * @param       user
	 * @return      boolean
	 */
	@Override
	public boolean isBankAccountAdded(User user) {
		List<BankAccountDetails> bankAccountDetails = bankAccountDetailsRepo.findByUser(user);
		return !bankAccountDetails.isEmpty();
	}

}
