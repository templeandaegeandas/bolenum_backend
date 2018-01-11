package com.bolenum.services.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.Privilege;
import com.bolenum.repo.common.PrivilegeRepo;

/**
 * @Author himanshu
 * @Date 08-Sep-2017
 * 
 */

@Service
public class PrivilegeServiceImpl implements PrivilegeService {

	private static final Logger logger = LoggerFactory.getLogger(PrivilegeServiceImpl.class);

	@Autowired
	private PrivilegeRepo privilegeRepo;

	public Privilege savePrivilege(Privilege privilege) {
		return privilegeRepo.saveAndFlush(privilege);

	}

	@Override
	public Boolean deletePrivilege(Long id) {
		Privilege privilege = privilegeRepo.findById(id);
		if (privilege != null) {
			privilegeRepo.delete(id);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Privilege findPrivilegeById(Long id) {
		return privilegeRepo.findById(id);
	}

	@Override
	public Privilege findOrCreate(Privilege privilege) {
		Privilege p = findByName(privilege.getName().trim());
		if (p == null) {
			logger.debug("privalege p is null ");
			return savePrivilege(privilege);
		}
		return savePrivilege(p);
	}

	@Override
	public Privilege findByName(String name) {
		return privilegeRepo.findByNameIgnoreCase(name);
	}

	@Override
	public Set<Privilege> findAllPrevileges() {
		List<Privilege> listOfPrivilege = privilegeRepo.findAll();
		return new HashSet<>(listOfPrivilege);
	}

}
