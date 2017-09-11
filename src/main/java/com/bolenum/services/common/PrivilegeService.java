package com.bolenum.service.common;

import com.bolenum.model.Privilege;

/**
 * @Author himanshu
 * @Date 11-Sep-2017
 */ 
public interface PrivilegeService {

	public Privilege savePrivilege(Privilege privilege);

	public Boolean deletePrivilege(Long id);

	public Privilege viewPrivilege(Long id);

}
