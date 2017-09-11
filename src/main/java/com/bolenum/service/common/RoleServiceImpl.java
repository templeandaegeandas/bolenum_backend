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
	public Role saveRole(Role role) {
		return roleRepo.saveAndFlush(role);

	}

	@Override
	public Boolean deleteRole(Long id) {
		Role role = roleRepo.findById(id);
		if (role != null) {
			roleRepo.delete(id);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Role viewRole(Long id) {
		Role role = roleRepo.findById(id);
		if (role != null) {
			return role;
		} else {
			return null;
		}
	}

	@Override
	public Role updateRole(Long id) {
		Role role = roleRepo.findById(id);
		if (role != null) {
			return role;
		} else {
			return null;
		}
	}
	

}