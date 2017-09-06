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
	public void saveRole(Privilege privilege) {
		
		// TODO Auto-generated method stub
		privilegeRepo.saveAndFlush(privilege);
	}
	
}
