package com.bolenum.services.user;

import com.bolenum.model.User;

/**
 * 
 * @Author Himanshu
 * @Date 11-Sep-2017
 */

public interface UserService {
	public void RegisterUser(User user) ;
	public void saveUser(User user);
	public boolean verifyUserToken(String token);
	public Boolean userIsExist(User user);
	public boolean userIsAlreadyRegistered(User user);
}
