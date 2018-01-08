package com.bolenum.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.bolenum.model.SubscribedUser;
import com.bolenum.repo.user.SubscribedUserRepo;

@Service
public class SubscribedUserServiceImpl implements SubscribedUserService {

	@Autowired
	private SubscribedUserRepo subscribedUserRepo;
	
	
	@Override
	public SubscribedUser validateSubscribedUser(String email) {
		return subscribedUserRepo.findByEmail(email);
	}

	@Override
	public SubscribedUser saveSubscribedUser(String email) {
		SubscribedUser subscribedUser=new SubscribedUser();
		subscribedUser.setEmail(email);
		subscribedUser.setIsSubscribed(true);
		return subscribedUserRepo.save(subscribedUser);
	}

	@Override
	public Page<SubscribedUser> getSubscribedUserList(int pageNumber, int pageSize, String sortBy, String sortOrder) {
		Pageable page = new PageRequest(pageNumber, pageSize, Direction.DESC, sortBy);
		return subscribedUserRepo.findAll(page);
	}
}
