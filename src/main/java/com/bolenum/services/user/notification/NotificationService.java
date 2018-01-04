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
	public Notification saveNotification(User buyer, User seller, String msg);

	public boolean sendNotification(User user, String message);

	public Page<Notification> getNotification(User receiver, int pageNumber, int pageSize);

	boolean sendNotificationForDispute(User user, String message);

	public Page<Notification> getListOfNotification(User user, int pageNumber, int pageSize, String sortOrder,
			String sortBy);

	public Notification setActionOnNotifiction(Notification notification);

	public Notification getRequestedNotification(Long id);

}
