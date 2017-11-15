package com.bolenum.repo.common;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bolenum.enums.DocumentStatus;
import com.bolenum.model.User;
import com.bolenum.model.UserKyc;

public interface KYCRepo extends JpaRepository<UserKyc, Long>{
	
    @Query("select k from UserKyc k where (k.user.firstName like %:searchData% or k.user.lastName like %:searchData% or k.user.emailId like %:searchData%) and k.documentStatus = :documentStatus")
	Page<UserKyc> findByDocumentStatus(@Param("documentStatus") DocumentStatus documentStatus, @Param("searchData") String searchData, Pageable pageable);

	List<UserKyc> findByUser(User user);
}
