package com.bolenum.repo.common.erc20token;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.User;
import com.bolenum.model.erc20token.UserErc20Token;

public interface UserErc20TokenRepository extends JpaRepository<UserErc20Token, Long> {

	UserErc20Token findByTokenNameAndUser(String tokenName, User user);
}
