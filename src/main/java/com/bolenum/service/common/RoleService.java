package com.bolenum.service.common;
import  com.bolenum.model.Role;


/**
 * @Author himanshu
 * @Date 11-Sep-2017
 */ 
public interface RoleService {
	
	public Role saveRole(Role role);

	public Boolean deleteRole(Long id);

	public Role viewRole(Long id);



}
