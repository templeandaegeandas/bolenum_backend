package com.bolenum.services.common;

import java.util.Set;

import com.bolenum.model.Privilege;

/**
 * @Author himanshu
 * @Date 11-Sep-2017
 */ 
public interface PrivilegeService {

	/**
	 * This method is use to save Privilege
	 * @param privilege
	 * @return
	 */
	public Privilege savePrivilege(Privilege privilege);

	/**
	 * This method is use to delete Privilege
	 * @param id
	 * @return
	 */
	public Boolean deletePrivilege(Long id);

	/**
	 * This method is use to find Privilege By Id
	 * @param id
	 * @return
	 */
	public Privilege findPrivilegeById(Long id);
	/**
	 * This method is use to find By Name
	 * @param name
	 * @return
	 */
	public Privilege findByName(String name);
	/**
	 * This method is use to find Or Create
	 * @param privilege
	 * @return
	 */
	public Privilege findOrCreate(Privilege privilege);
	
	/**
	 * This method is use to find All Previleges
	 * @return
	 */
	public Set<Privilege> findAllPrevileges();

}
