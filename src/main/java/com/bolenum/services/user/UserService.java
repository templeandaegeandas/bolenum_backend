package com.bolenum.services.user;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.bolenum.dto.common.EditUserForm;
import com.bolenum.dto.common.PasswordForm;
import com.bolenum.exceptions.InvalidPasswordException;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.PersistenceException;
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
	public User uploadImage(MultipartFile file, Long userId) throws IOException, PersistenceException, MaxSizeExceedException;
}
