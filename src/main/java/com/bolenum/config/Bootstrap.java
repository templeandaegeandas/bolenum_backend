package com.bolenum.config;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.bolenum.model.Privilege;
import com.bolenum.model.Role;
import com.bolenum.model.User;
import com.bolenum.services.common.PrivilegeService;
import com.bolenum.services.common.RoleService;
import com.bolenum.services.user.UserService;
import com.bolenum.util.PasswordEncoderUtil;

/**
 * @author chandan kumar singh
 * @date 12-Sep-2017
 */
@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserService userService;
	@Autowired
	private PrivilegeService privilegeService;

	@Autowired
	private PasswordEncoderUtil passwordEncoder;

	private Set<Privilege> privileges = new HashSet<>();

	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		addPrivileges();
		addRole();
		createAdmin();
	}

	/**
	 * @description addRole @param @return void @exception
	 */
	private void addRole() {
		Role role = new Role("ROLE_USER", "user role", privilegeService.findAllPrevileges());
		roleService.findOrCreate(role);
	}

	/**
	 * @description addPrivileges @param @return void @exception
	 */
	private void addPrivileges() {
		Privilege add = new Privilege("add", "adding");
		Privilege edit = new Privilege("edit", "editing");
		Privilege get = new Privilege("view", "showing");
		Privilege del = new Privilege("delete", "deleting");
		privileges.add(add);
		privileges.add(edit);
		privileges.add(get);
		privileges.add(del);
		privileges.forEach(Privilege -> privilegeService.findOrCreate(Privilege));
	}

	/**
	 * @description createAdmin @param @return void @exception
	 */
	private void createAdmin() {
		Role r = new Role("ROLE_ADMIN", "Admin role", privilegeService.findAllPrevileges());
		Role roleAdmin = roleService.findOrCreate(r);
		User admin = userService.findByEmail("admin@bolenum.com");
		if (admin == null) {
			User form = new User();
			form.setIsEnabled(true);
			form.setFirstName("bolenum");
			form.setEmailId("admin@bolenum.com");
			form.setPassword(passwordEncoder.encode("12345"));
			form.setRole(roleAdmin);
			userService.saveUser(form);
		} else {
			logger.debug("admin exist");
		}
	}

}
