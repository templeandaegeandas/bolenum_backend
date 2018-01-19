/*@Description Of interface
 * 
 * SubscribedUserService interface is responsible for below listed task: 
 *   
 *     Validate subscribed user
 *     Save subscribed user
 *     Get subscribed userList
 **/
package com.bolenum.services.user;


import org.springframework.data.domain.Page;

import com.bolenum.model.SubscribedUser;

public interface SubscribedUserService {
	
	/**@Description Use to validate subscribed user
	 * @param       email
	 * @return     SubscribedUser
	 */
	SubscribedUser validateSubscribedUser(String email);
    
	
	/**@Description Use to save subscribed user
	 * @param       email
	 * @return     SubscribedUser
	 */
	SubscribedUser saveSubscribedUser(String email);
    
	
	/**@Description Use to get subscribed user list
	 * @param       pageNumber
	 * @param       pageSize
	 * @param       sortBy
	 * @param       sortOrder
	 * @return      subscribed user list
	 */
	Page<SubscribedUser> getSubscribedUserList(int pageNumber, int pageSize, String sortBy, String sortOrder);

}
