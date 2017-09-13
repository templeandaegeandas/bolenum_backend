package com.bolenum.repo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bolenum.model.User;
import com.bolenum.model.AuthenticationToken;

@Repository("tokenRepository")

public interface TokenRepository extends JpaRepository<AuthenticationToken, String> {
	AuthenticationToken findByToken(String token);

	AuthenticationToken findByUser(User user);
}
