package com.bolenum.repo.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.constant.DocumentStatus;
import com.bolenum.model.UserKyc;

public interface KYCRepo extends JpaRepository<UserKyc, Long>{

	Page<UserKyc> findByDocumentStatusIn(DocumentStatus documentStatus, Pageable pageable);
}
