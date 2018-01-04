package com.bolenum.services.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.Role;
import com.bolenum.repo.common.RoleRepo;

@Service
public class RoleServiceImpl implements RoleService {
	private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
	@Autowired
	private RoleRepo roleRepo;

	@Override
	public Role saveRole(Role role) {
		logger.debug("save and flush role meth: " + role.getName());
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
		Role newRole = roleRepo.findByNameIgnoreCase(role.getName().trim());
		if (newRole == null) {
			logger.debug("find or create role: got new role");
			return saveRole(role);
		} else {
			logger.debug("find or create role: role exists");
			return saveRole(newRole);
		}
	}

	@Override
	public Role findByName(String name) {
		return roleRepo.findByNameIgnoreCase(name);
	}

}