package com.bolenum.repo.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

	/**
	 * This method is use to find Role by id
	 * @param id
	 * @return
	 */
	Role findById(Long id);

	/**
	 * This method is use find Role by nameignorecase
	 * @param name
	 * @return
	 */
	Role findByNameIgnoreCase(String name);

}
