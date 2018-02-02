package com.bolenum.repo.common;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.Privilege;

public interface PrivilegeRepo extends JpaRepository<Privilege, Long>{

	/**
	 * This method is use to find Privilege by id
	 * @param id
	 * @return
	 */
	Privilege findById(Long id);

	/**
	 * This method is use to find Privilege by nameignorecase
	 * @param name
	 * @return
	 */
	Privilege findByNameIgnoreCase(String name);
}
