package com.bolenum.services.common;

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
		System.out.println("save role ssssssss: "+role.getName());
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
	public Role findOrCreate(Role role) {
		System.out.println("role name: "+role.getName());
		Role newRole = roleRepo.findByName(role.getName().trim());
		System.out.println("newRole == null: "+(newRole == null));
		if (newRole == null) {
			System.out.println("---new role is null--------------------------");
			return saveRole(role);
		} else {
			return saveRole(newRole);
		}
	}

	@Override
	public Role findByName(String name) {
		return roleRepo.findByName(name);
	}

}