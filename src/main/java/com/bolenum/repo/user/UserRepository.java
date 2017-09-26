package com.bolenum.repo.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bolenum.constant.DocumentStatus;
import com.bolenum.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmailIdIgnoreCase(String emailId);
	
	/**
	 * This query returns all users list that are not deleted and not admin with searching according to firstName, lastName and emailId 
	 * @param searchData
	 * @param userId
	 * @param pageable
	 * @return List of Users
	 */
	@Query("select u from User u where (u.firstName like %:searchData% or u.lastName like %:searchData% or u.emailId like %:searchData%) and u.userId != :userId and u.isDeleted = false")
	Page<User> getUserListWithSearch(@Param("searchData") String searchData, @Param("userId") Long userId, Pageable pageable);
	
	/**
	 * This query returns all users list that are not deleted and kyc document status is submitted with searching according to firstName, lastName and emailId 
	 * @param searchData
	 * @param documentStatus
	 * @param pageable
	 * @return List of users who have kyc documents status is submitted
	 */
	@Query("select u from User u where (u.firstName like %:searchData% or u.lastName like %:searchData% or u.emailId like %:searchData%) and u.userKyc.documentStatus = :documentStatus and u.isDeleted = false")
	Page<User> getNewlySubmittedKycListWIthSearch(@Param("searchData") String searchData, @Param("documentStatus") DocumentStatus documentStatus, Pageable pageable);

	User findByMobileNumber(String mobileNumber);
}
