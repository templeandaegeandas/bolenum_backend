package com.bolenum.services.common;

import java.util.List;
import com.bolenum.model.Privilege;

/**
 * @Author himanshu
 * @Date 11-Sep-2017
 */ 
public interface PrivilegeService {

	public Privilege savePrivilege(Privilege privilege);

	public Boolean deletePrivilege(Long id);

	public Privilege findPrivilegeById(Long id);
	public Privilege findByName(String name);
	public Privilege findOrCreate(Privilege privilege);
	

	public List<Privilege> findAllPrevileges();

}
