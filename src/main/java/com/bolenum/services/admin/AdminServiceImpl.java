package com.bolenum.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.bolenum.constant.DocumentStatus;
import com.bolenum.model.User;
import com.bolenum.repo.user.UserRepository;

/**
 * 
 * @author vishal_kumar
 * @date 15-sep-2017
 *
 */

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public Page<User> getUsersList(int pageNumber, int pageSize, String sortBy, String sortOrder, String searchData, User user) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		}
		else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		Page<User> userList = userRepository.findByFirstNameContainingOrLastNameContainingOrEmailIdContainingAndUserIdNotIn(searchData, searchData, searchData, user.getUserId(), pageRequest);
		return userList;
	}

	
	@Override
	public User getUserById(Long userId) {
		return userRepository.findOne(userId);
	}


	@Override
	public Page<User> getSubmitedKycList(int pageNumber, int pageSize) {
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, Direction.DESC, "createdOn");
		Page<User> userList = userRepository.findByUserKycDocumentStatusIn(DocumentStatus.SUBMITTED, pageRequest);
		return userList;
	}
}
