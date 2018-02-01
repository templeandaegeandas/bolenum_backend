/**
 * 
 */
package com.bolenum.services.user.notification;

import org.springframework.data.domain.Page;

import com.bolenum.enums.NotificationType;
import com.bolenum.model.User;
import com.bolenum.model.notification.Notification;

/**
 * @author chandan kumar singh
 * @date 31-Oct-2017
 */
public interface NotificationService {
	
	/**
	 * This method is use to save Notification
	 * @param sender
	 * @param receiver
	 * @param msg
	 * @param notificationRelationId
	 * @param notificationType
	 * @return
	 */
	public Notification saveNotification(User sender, User receiver, String msg, Long notificationRelationId, NotificationType notificationType);

	/**
	 * This method is use to send Notification
	 * @param user
	 * @param message
	 * @param subject
	 * @return
	 */
	public boolean sendNotification(User user, String message, String subject);

	/**
	 * This method is use to get Notification
	 * @param receiver
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Notification> getNotification(User receiver, int pageNumber, int pageSize);

	/**
	 * This method is use to get List Of Notification
	 * @param user
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @return
	 */
	public Page<Notification> getListOfNotification(User user, int pageNumber, int pageSize, String sortOrder,
			String sortBy);

	/**
	 * This method is use to set Action On Notifiction
	 * @param id
	 */
	public void setActionOnNotifiction(Long id);

	/**
	 * This method is use to get Requested Notification
	 * @param id
	 * @return
	 */
	public Notification getRequestedNotification(Long id);

	/**
	 * This method is use to count UnSeen Notification
	 * @param user
	 * @return
	 */
	public Long countUnSeenNotification(User user);

	/**
	 * This method is use to change Notifications Status
	 * @param arrayOfNotification
	 */
	public void changeNotificationsStatus(Long[] arrayOfNotification);
  
}
