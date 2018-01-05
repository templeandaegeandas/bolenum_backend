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

	/**
	 * 
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

	/**
	 * 	
	 */
	@Override
	public User getUserById(Long userId) {
		return userRepository.findOne(userId);
	}

	@Override
	public boolean adminWithdrawCryptoAmount(User user, String tokenName, Double withdrawAmount, String toAddress) {
		return btcWalletService.adminWithdrawCryptoAmount(user, tokenName, withdrawAmount, toAddress);
	}

	@Override
	public Future<Boolean> adminWithdrawErc20TokenAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		return btcWalletService.adminWithdrawErc20TokenAmount(user, tokenName, withdrawAmount, toAddress);
	}

	@Override
	public boolean adminValidateErc20WithdrawAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		Erc20Token erc20Token = erc20TokenRepository.findByCurrencyCurrencyAbbreviation(tokenName);
		return btcWalletService.adminValidateErc20WithdrawAmount(user, "ETH", withdrawAmount, toAddress, erc20Token);
	}

	@Override
	public boolean adminValidateCryptoWithdrawAmount(User user, String tokenName, Double withdrawAmount,
			String toAddress) {
		return btcWalletService.adminValidateCryptoWithdrawAmount(user, tokenName, withdrawAmount, toAddress);
	}

	@Override
	public List<User> getListOfUsers() {

		return userRepository.findByIsEnabled(true);
	}

	@Override
	public Double findTotalDepositBalance(String tokenName) {
		return userCoinRepository.findTotalDepositBalance(tokenName);
	}
}
