package com.bolenum.services.user;

import com.bolenum.model.User;

/**
 * 
 * @Author Himanshu
 * @Date 11-Sep-2017
 */

public interface UserService {
	public void registerUser(User user) ;
	public User saveUser(User user);
	public boolean verifyUserToken(String token);
	public User findByEmail(String email);
}
