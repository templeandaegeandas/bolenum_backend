package com.bolenum.repo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.AuthenticationToken;
import com.bolenum.model.UserActivity;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long>{

	UserActivity findByAuthenticationToken(AuthenticationToken authenticationToken);
}
