package com.bolenum.repo.common;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;

/**
 * 
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public interface AuthenticationTokenRepo extends JpaRepository<AuthenticationToken, Serializable> {
	AuthenticationToken findByToken(String token);

	AuthenticationToken findByUser(User user);
	
}
