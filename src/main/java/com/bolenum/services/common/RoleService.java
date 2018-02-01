package com.bolenum.services.common;
import  com.bolenum.model.Role;


/**
 * @Author himanshu
 * @Date 11-Sep-2017
 */ 
public interface RoleService {
	
	/**
	 * This method is use to save Role
	 * @param role
	 * @return
	 */
	public Role saveRole(Role role);

	/**
	 * This method is use to delete Role
	 * @param id
	 * @return
	 */
	public Boolean deleteRole(Long id);

	/**
	 * This method is use to view Role
	 * @param id
	 * @return
	 */
	public Role viewRole(Long id);
	/**
	 * This method is use to find By Name
	 * @param name
	 * @return
	 */
	public Role findByName(String name);
	/**
	 * This method is use to find Or Create
	 * @param role
	 * @return
	 */
	public Role findOrCreate(Role role);

}
