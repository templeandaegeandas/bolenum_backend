package com.bolenum.services.user;

import com.bolenum.dto.common.EditUserForm;
import com.bolenum.dto.common.PasswordForm;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.model.User;

/**
 * 
 * @Author Himanshu
 * @Date 11-Sep-2017
 */

public interface UserService {
	public void registerUser(User user) ;
	public User saveUser(User user);
	public User findByEmail(String email);
	public void reRegister(User isUserExist);
	boolean changePassword(User user, PasswordForm passwordForm) throws InvalidPasswordException;
	User updateUserProfile(EditUserForm EditUserForm, User user);
	public User findByUserId(Long id);
}
