package com.bolenum.services.user;

import com.bolenum.model.SubscribedUser;

public interface SubscribedUserService {

	SubscribedUser validateSubscribedUser(String email);

	SubscribedUser saveSubscribedUser(String email);

}
