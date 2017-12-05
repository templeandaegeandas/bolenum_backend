package com.bolenum.repo.common;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bolenum.enums.TokenType;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.User;

/**
 * 
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public interface AuthenticationTokenRepo extends JpaRepository<AuthenticationToken, Serializable> {
	AuthenticationToken findByToken(String token);

	List<AuthenticationToken> findByUserAndTokentype(User user,TokenType tokentype);
	List<AuthenticationToken> findByUserAndIsDeleted(User user, boolean isDeleted);
	List<AuthenticationToken> findByUserAndIsDeletedAndTokentype(User user, boolean isDeleted,TokenType token);

	@Query("Select count(distinct a.user) from AuthenticationToken a where a.tokentype=?1 and a.createdOn <= ?2 and a.createdOn >= ?3")
	Long countAuthenticationTokenByTokentypeCreatedOnBetween(TokenType tokentype ,Date startDate,Date endDate);
 	
}
