package com.bolenum.repo.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.UserKyc;

public interface KYCRepo extends JpaRepository<UserKyc, Long>{

}
