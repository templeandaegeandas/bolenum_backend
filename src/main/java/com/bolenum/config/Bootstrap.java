package com.bolenum.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${bolenum.ethwallet.location}")
	private String ethWalletLocation; // ethereum wallet file location

	private Set<Privilege> privileges = new HashSet<>();

	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		addPrivileges();
		addRole();
		createAdmin();

		// create initial directories
		createInitDirectories();
	}

	/**
	 * this will create ethereum wallet location at the time of application start
	 * @description createInitDirectories
	 * @param 
	 * @return void
	 * @exception 
	 * 
	 */
	private void createInitDirectories() {
		Path ethWallet = Paths.get(ethWalletLocation);
		if (!Files.exists(ethWallet)) {
			if (new File((ethWalletLocation)).mkdirs()) {
				logger.debug("ethereum wallet location created");
			} else {
				logger.debug("ethereum wallet location creation failed");
			}
		} else {
			logger.debug("ethereum wallet location exists");
		}
		
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
