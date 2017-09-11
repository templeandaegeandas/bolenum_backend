package com.bolenum.services.user;

import com.bolenum.model.User;

/**
 * 
 * @Author Himanshu
 * @Date 11-Sep-2017
 */

public interface UserService {
	public void registerUser(User user) ;
	public void saveUser(User user);
	public boolean verifyUserToken(String token);
	public boolean isUserAlreadyRegistered(User user);
	public boolean isUserExist(User user);
	public void sendToken(User user);
}
