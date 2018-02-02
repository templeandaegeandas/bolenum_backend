package com.bolenum.repo.common.coin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bolenum.model.User;
import com.bolenum.model.coin.UserCoin;

public interface UserCoinRepository extends JpaRepository<UserCoin, Long> {

	/**
	 * This method is use to find UserCoin By Token Name And User
	 * @param tokenName
	 * @param user
	 * @return
	 */
	UserCoin findByTokenNameAndUser(String tokenName, User user);

	/**
	 * This method is use to find UserCoin By Wallet Address
	 * @param walletAddress
	 * @return
	 */
	UserCoin findByWalletAddress(String walletAddress);

	@Query("SELECT SUM(uc.balance) FROM UserCoin uc WHERE uc.tokenName=:tokenName and uc.user.role.name ='ROLE_USER'")
	Double findTotalDepositBalanceOfUser(@Param("tokenName") String tokenName);
}
