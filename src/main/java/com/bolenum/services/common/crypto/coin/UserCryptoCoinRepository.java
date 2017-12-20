package com.bolenum.services.common.crypto.coin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.User;
import com.bolenum.model.crypto.coin.UserCryptoCoin;

public interface UserCryptoCoinRepository extends JpaRepository<UserCryptoCoin, Long> {

	UserCryptoCoin findByWalletAddress(String walletAddress);

	UserCryptoCoin findByUserAndTokenName(User user, String tokenName);
}
