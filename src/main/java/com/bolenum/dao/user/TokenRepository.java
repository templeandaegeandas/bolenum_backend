package com.bolenum.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bolenum.model.VerificationToken;

@Repository("tokenRepository")

public interface TokenRepository extends JpaRepository<VerificationToken, String> {
	VerificationToken findByToken(String token);
}
