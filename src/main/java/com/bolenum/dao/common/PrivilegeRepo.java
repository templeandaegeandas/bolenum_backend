package com.bolenum.dao.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.Privilege;

public interface PrivilegeRepo extends JpaRepository<Privilege, Long>{

	Privilege findById(Long id);

	Privilege findByName(String name);



}