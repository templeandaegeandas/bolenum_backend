package com.bolenum.service.common;
import  com.bolenum.model.Role;

public interface RoleService {
	
	public Role saveRole(Role role);

	public Boolean deleteRole(Long id);

	public Role viewRole(Long id);

	public Role updateRole(Long id);

}
