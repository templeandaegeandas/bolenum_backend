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

	Page<User> getUsersList(int pageNumber, int pageSize, String sortBy, String sortOrder, String searchData,
			User user);

	User getUserById(Long userId);

	boolean adminWithdrawCryptoAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	Future<Boolean> adminWithdrawErc20TokenAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	boolean adminValidateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	boolean adminValidateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount, String toAddress);

	List<User> getListOfUsers();
	
	Double findTotalDepositBalanceOfUser(String tokenName);

}
