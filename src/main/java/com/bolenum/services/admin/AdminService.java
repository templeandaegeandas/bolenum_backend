package com.bolenum.services.admin;

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

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param user
	 * @return Page
	 */
	Page<User> getUsersList(int pageNumber, int pageSize, String sortBy, String sortOrder, String searchData, User user);
	
	/**
	 * 
	 * @param userId
	 * @return User
	 */
	User getUserById(Long userId);
	
	String createAdminHotWallet(String uuid);

	String getAdminWalletBalnce(String uuid);
	
	boolean adminWithdrawCryptoAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	Future<Boolean> adminWithdrawErc20TokenAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	boolean adminValidateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	boolean adminValidateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

}
