package com.bolenum.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.dao.common.PrivilegeRepo;
import com.bolenum.model.Privilege;

/**
 * @Author himanshu
 * @Date 08-Sep-2017
 * 
 */

@Service
public class PrivilegeServiceImpl implements PrivilegeService {

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
	public Privilege viewPrivilege(Long id) {
		Privilege privilege = privilegeRepo.findById(id);
		if (privilege != null) {
			return privilege;
		} else {
			return null;
		}
	}

	@Override
	public Privilege updatePrivilege(Long id) {
		Privilege privilege = privilegeRepo.findById(id);
		if (privilege != null) {
			return privilege;
		} else {
			return null;
		}
	}

}
