package com.bolenum.services.user;

import com.bolenum.model.User;

/**
 * 
 * @Author Himanshu
 * @Date 11-Sep-2017
 */

public interface UserService {
	public void registerUser(User user) ;
	public boolean verifyUserToken(String token);
	public User findByEmail(User user);
	public void sendToken(User user);
	public void sendTokenIfUserAlreadyExist(User isUserExist);
}
