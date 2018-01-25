/*@Description Of interface
 * 
 * AdminService interface is responsible for below listed task: 
 *   
 *     Get users list
 *     Get user by id
 *     Admin withdraw CRYPTO amount
 *     Admin withdraw Erc20Token amount
 *     Admin validateErc20 withdraw amount
 *     Get list of users
 *     Find total deposit balance of user
 **/


package com.bolenum.services.admin;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.data.domain.Page;

import com.bolenum.model.User;

/**
 * 
 * @author Vishal Kumar
 * @date 18-sep-2017
 *
 */

public interface AdminService {
	
	/**@description use to get user list
	 *@param pageNumber
	 *@param pageSize
	 *@param sortBy
	 *@param sortOrder
	 *@param searchData
	 *@param user
	 *@return user list
	 */
	Page<User> getUsersList(int pageNumber, int pageSize, String sortBy, String sortOrder, String searchData,
			User user);
	
	
	/**@description use to get user by Id
	 * @param userid
	 * @return user
	 */
	User getUserById(Long userId);

	
	/**@description use to get admin Withdraw Crypto Amount
	 *@param user
	 *@param tokenName
	 *@param withdrawAmount
	 *@param toAddress
	 *@return boolean
	 */
	boolean adminWithdrawCryptoAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	/**@description use to get admin Withdraw ERC20 Amount
	 *@param user
	 *@param tokenName
	 *@param withdrawAmount
	 *@param toAddress
	 *@return boolean
	 */
	Future<Boolean> adminWithdrawErc20TokenAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	
	/**@description use to validate admin Withdraw ERC20 Amount
	 *@param user
	 *@param tokenName
	 *@param withdrawAmount
	 *@param toAddress
	 *@return boolean
	 */
	boolean adminValidateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	

	/**@description use to validate admin Withdraw crypto Amount
	 *@param user
	 *@param tokenName
	 *@param withdrawAmount
	 *@param toAddress
	 *@return boolean
	 */
	boolean adminValidateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	
	/**@description use to get user list
	 * @return user list
	 */
	List<User> getListOfUsers();
	
	
	/**@description use to get Total Deposit Balance Of User
	 * @return deposite balance
	 */
	Double findTotalDepositBalanceOfUser(String tokenName);

}
