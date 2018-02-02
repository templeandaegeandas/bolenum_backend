package com.bolenum.repo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.SubscribedUser;

public interface SubscribedUserRepo extends JpaRepository<SubscribedUser, Long> {

	/**
	 * This method is use to find By Email
	 * @param email
	 * @return
	 */
	SubscribedUser findByEmail(String email);

}
