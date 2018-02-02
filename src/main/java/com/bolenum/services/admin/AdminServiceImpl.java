/*@Description Of class
 * 
 * AdminServiceImpl class is responsible for below listed task: 
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.bolenum.model.User;
import com.bolenum.model.coin.Erc20Token;
import com.bolenum.repo.common.coin.Erc20TokenRepository;
import com.bolenum.repo.common.coin.UserCoinRepository;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.services.user.wallet.BTCWalletService;

/**
 * 
 * @author vishal_kumar
 * @date 15-sep-2017
 *
 */

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserCoinRepository userCoinRepository;

	@Autowired
	private BTCWalletService btcWalletService;

	@Autowired
	private Erc20TokenRepository erc20TokenRepository;

	/**@Description:To get list of all the users 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortBy
	 * @param sortOrder
	 * @param searchData
	 * @return user, pageRequest
	 */
	@Override
	public Page<User> getUsersList(int pageNumber, int pageSize, String sortBy, String sortOrder, String searchData,
			User user) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return userRepository.getUserListWithSearch(searchData, user.getUserId(), pageRequest);
	}

	/**@description use to get user by Id
	 * @param userid
	 * @return user
	 */
	@Override
	public User getUserById(Long userId) {
		return userRepository.findOne(userId);
	}
    
	/**@description use to get admin Withdraw Crypto Amount
	 *@param user
	 *@param tokenName
	 *@param withdrawAmount
	 *@param toAddress
	 *@return boolean
	 */
	@Override
	public boolean adminWithdrawCryptoAmount(User user, String tokenName, Double withdrawAmount, String toAddress) {
		return btcWalletService.adminWithdrawCryptoAmount(user, tokenName, withdrawAmount, toAddress);
	}

	
	/**@description use to get admin Withdraw ERC20 Amount
	 *@param user
	 *@param tokenName
	 *@param withdrawAmount
	 *@param toAddress
	 *@return Future<Boolean>
	 */
	@Override
	public Future<Boolean> adminWithdrawErc20TokenAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		return btcWalletService.adminWithdrawErc20TokenAmount(user, tokenName, withdrawAmount, toAddress);
	}
	
	
	/**@description use to validate admin Withdraw ERC20 Amount
	 *@param user
	 *@param tokenName
	 *@param withdrawAmount
	 *@return boolean
	 */
	@Override
	public boolean adminValidateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		Erc20Token erc20Token = erc20TokenRepository.findByCurrencyCurrencyAbbreviation(tokenName);
		if(erc20Token == null) {
			return false;
		}
		return btcWalletService.adminValidateErc20WithdrawAmount(user, "ETH", withdrawAmount, toAddress, erc20Token);
	}
	
	
	/**@description use to validate admin Withdraw crypto Amount
	 *@param user
	 *@param tokenName
	 *@param withdrawAmount
	 *@param toAddress
	 *@return boolean
	 */
	@Override
	public boolean adminValidateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		return btcWalletService.adminValidateCryptoWithdrawAmount(user, tokenName, withdrawAmount, toAddress);
	}
    
	/**@description use to get user list
	 * @return user list
	 */
	@Override
	public List<User> getListOfUsers() {

		return userRepository.findByIsEnabled(true);
	}
	
	
	/**@description use to get Total Deposit Balance Of User
	 * @return deposite balance
	 */
	@Override
	public Double findTotalDepositBalanceOfUser(String tokenName) {
		Double bal = userCoinRepository.findTotalDepositBalanceOfUser(tokenName);
		return bal == null ? 0.0 : bal;
	}
}
