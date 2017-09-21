package com.bolenum.repo.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.constant.DocumentStatus;
import com.bolenum.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmailIdIgnoreCase(String emailId);
	
	Page<User> findByUserIdIsNotIn(Long userId, Pageable pageable);
	
	Page<User> findByUserKycDocumentStatusIn(DocumentStatus documentStatus, Pageable pageable);

}
