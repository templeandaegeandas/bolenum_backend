package com.bolenum.services.user;

import org.springframework.beans.factory.annotation.Autowired;
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
		System.out.println("hhihihih");
		subscribedUser.setEmail(email);
		subscribedUser.setIsSubscribed(true);
		System.out.println("hhihihih");
		return subscribedUserRepo.save(subscribedUser);
	}
}
