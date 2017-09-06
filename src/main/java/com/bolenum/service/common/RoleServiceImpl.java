package com.bolenum.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.dao.common.RoleRepo;
import com.bolenum.model.Role;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
    private RoleRepo roleRepo;
	@Override
	public void saveRole(Role role) {
		
		// TODO Auto-generated method stub
		roleRepo.saveAndFlush(role);
		
	}

}