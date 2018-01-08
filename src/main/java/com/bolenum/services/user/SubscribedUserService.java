package com.bolenum.services.user;


import org.springframework.data.domain.Page;

import com.bolenum.model.SubscribedUser;

public interface SubscribedUserService {

	SubscribedUser validateSubscribedUser(String email);

	SubscribedUser saveSubscribedUser(String email);

	Page<SubscribedUser> getSubscribedUserList(int pageNumber, int pageSize, String sortBy, String sortOrder);

}
