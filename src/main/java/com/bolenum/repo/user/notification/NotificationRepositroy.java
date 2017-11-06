/**
 * 
 */
package com.bolenum.repo.user.notification;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.User;
import com.bolenum.model.notification.Notification;

/**
 * @author chandan kumar singh
 * @date 31-Oct-2017
 */
public interface NotificationRepositroy extends JpaRepository<Notification, Serializable> {
	public Page<Notification> findByBuyerAndIsDeleted(User buyer, boolean isDeleted, Pageable pagable);

	public Page<Notification> findBySellerAndIsDeleted(User seller, boolean isDeleted, Pageable pagable);
	
}