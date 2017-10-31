/**
 * 
 */
package com.bolenum.services.user.notification;

import org.springframework.data.domain.Page;

import com.bolenum.model.User;
import com.bolenum.model.notification.Notification;

/**
 * @author chandan kumar singh
 * @date 31-Oct-2017
 */
public interface NotificationService {
	
	public boolean sendNotification(User sender, User receiver, String message);

	public Page<Notification> getNotification(User receiver, int pageNumber, int pageSize);

	public Page<Notification> getNotification(int pageNumber, int pageSize);
}
