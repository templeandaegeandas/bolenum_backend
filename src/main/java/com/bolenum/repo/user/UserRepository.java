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
	
	@Query("select u from User u where (u.firstName like %:searchData% or u.lastName like %:searchData% or u.emailId like %:searchData%) and u.userId != :userId and u.isDeleted = false")
	Page<User> getUserListWithSearch(@Param("searchData") String searchData, @Param("userId") Long userId, Pageable pageable);
	@Query("select u from User u where (u.firstName like %:searchData% or u.lastName like %:searchData% or u.emailId like %:searchData%) and u.userKyc.documentStatus = :documentStatus and u.isDeleted = false")
	Page<User> getNewlySubmittedKycListWIthSearch(@Param("searchData") String searchData, @Param("documentStatus") DocumentStatus documentStatus, Pageable pageable);

}
