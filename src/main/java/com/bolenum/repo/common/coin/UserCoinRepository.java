package com.bolenum.repo.common.coin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.User;
import com.bolenum.model.coin.UserCoin;

public interface UserCoinRepository extends JpaRepository<UserCoin, Long> {

	UserCoin findByTokenNameAndUser(String tokenName, User user);
	
	UserCoin findByWalletAddress(String walletAddress);
}
