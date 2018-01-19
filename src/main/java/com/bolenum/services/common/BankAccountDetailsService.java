/*@Description Of interface
 * 
 * BankAccountDetailsService interface is responsible for below listed task: 
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

import com.bolenum.dto.common.EditUserBankDetailsForm;
import com.bolenum.model.BankAccountDetails;
import com.bolenum.model.User;
/**
 * 
 * @Author Himanshu
 * @Date 24-Sep-2017
 */
public interface BankAccountDetailsService {
	
	/**@description use to get bank account details by using account number
	 * @param       accountNumber
	 * @return      bank account details
	 */
	public BankAccountDetails findByAccountNumber(String accountNumber);
	
	
	/**@description use to save bank account details
	 * @param       bank account details
	 * @return      bank account details
	 */
	public BankAccountDetails saveBankDetails(BankAccountDetails bankAccountDetails);
	
	
	
	/**@description use to update bank account details
	 * @param       editUserBankDetailsForm          
	 * @param       isUserBankDetailsExist
	 * @return      bank account details
	 */
	public BankAccountDetails updateUserBankDetails(EditUserBankDetailsForm editUserBankDetailsForm,BankAccountDetails isUserBankDetailsExist);

	
	/**@description use to find bank account details by user
	 * @param       user
	 * @return      list of bank account details
	 */
	public List<BankAccountDetails> findByUser(User user);
	
	
	/**@description use to find bank account details by ID
	 * @param       id
	 * @return      list of bank account details
	 */
	public BankAccountDetails findByID(Long id);
	
	
	/**@description use to find primary bank account details by user
	 * @param       user
	 * @return      list of bank account details
	 */
	public BankAccountDetails primaryBankAccountDetails(User user);
	
	/**@description use to check bank account added or not
	 * @param       user
	 * @return      boolean
	 */
	public boolean isBankAccountAdded(User user);

}
