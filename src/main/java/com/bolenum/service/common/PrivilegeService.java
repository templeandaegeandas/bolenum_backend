package com.bolenum.service.common;

import com.bolenum.model.Privilege;


public interface PrivilegeService {


	public Privilege savePrivilege(Privilege privilege);

	public Boolean deletePrivilege(Long id);
	
	public Privilege viewPrivilege(Long id);

	public Privilege updatePrivilege(Long id);

	
}
