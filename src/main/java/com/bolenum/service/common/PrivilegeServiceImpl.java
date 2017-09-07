package com.bolenum.service.common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.dao.common.PrivilegeRepo;
import com.bolenum.model.Privilege;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {
	
	@Autowired
	private PrivilegeRepo privilegeRepo ;

	
	@Override
	public void savePrivilege(Privilege privilege) {
		// TODO Auto-generated method stub
		privilegeRepo.saveAndFlush(privilege);
		
	}
	
	@Override
	public void deletePrivilege(String name) {
		// TODO Auto-generated method stub
		Privilege privilege=privilegeRepo.findByName(name);
		privilegeRepo.delete(privilege);
		
	}



	
}
