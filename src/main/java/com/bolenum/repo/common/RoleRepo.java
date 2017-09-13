package com.bolenum.repo.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

	Role findById(Long id);

	Role findByNameIgnoreCase(String name);

}
