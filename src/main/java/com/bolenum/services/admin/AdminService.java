package com.bolenum.services.admin;

import org.springframework.data.domain.Page;

import com.bolenum.model.User;

/**
 * 
 * @author Vishal Kumar
 * @date 18-sep-2017
 *
 */

public interface AdminService {

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param user
	 * @return Page
	 */
	Page<User> getUsersList(int pageNumber, int pageSize, User user);
	
	/**
	 * 
	 * @param userId
	 * @return User
	 */
	User getUserById(Long userId);

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return Page<User>
	 */
	Page<User> getSubmitedKycList(int pageNumber, int pageSize);

}
