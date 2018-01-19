/*@Description Of class
 * 
 * SubscribedUserServiceImpl class is responsible for below listed task: 
 *   
 *     Validate subscribed user
 *     Save subscribed user
 *     Get subscribed userList
 **/

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
	
	/**@Description Use to validate subscribed user
	 * @param       email
	 * @return     SubscribedUser
	 */
	@Override
	public SubscribedUser validateSubscribedUser(String email) {
		return subscribedUserRepo.findByEmail(email);
	}
	
	
	/**@Description Use to save subscribed user
	 * @param       email
	 * @return     SubscribedUser
	 */
	@Override
	public SubscribedUser saveSubscribedUser(String email) {
		SubscribedUser subscribedUser=new SubscribedUser();
		subscribedUser.setEmail(email);
		subscribedUser.setIsSubscribed(true);
		return subscribedUserRepo.save(subscribedUser);
	}

	/**@Description Use to get subscribed user list
	 * @param       pageNumber
	 * @param       pageSize
	 * @param       sortBy
	 * @param       sortOrder
	 * @return      subscribed user list
	 */
	@Override
	public Page<SubscribedUser> getSubscribedUserList(int pageNumber, int pageSize, String sortBy, String sortOrder) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable page = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return subscribedUserRepo.findAll(page);
	}
}
