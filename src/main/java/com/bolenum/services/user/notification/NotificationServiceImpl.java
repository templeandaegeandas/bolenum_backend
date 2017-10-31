/**
 * 
 */
package com.bolenum.services.user.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.bolenum.model.User;
import com.bolenum.model.notification.Notification;
import com.bolenum.repo.user.notification.NotificationRepositroy;

/**
 * @author chandan kumar singh
 * @date 31-Oct-2017
 */
@Service
public class NotificationServiceImpl implements NotificationService {
	private Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

	@Autowired
	private NotificationRepositroy notificationRepositroy;

	/**
	 * to send the notification
	 */
	@Override
	public boolean sendNotification(User sender, User receiver, String message) {
		return false;
	}

	/**
	 * to get the notifications of a user
	 * 
	 * @param pagenumber
	 * @param pagesize
	 */
	@Override
	public Page<Notification> getNotification(User receiver, int pageNumber, int pageSize) {
		logger.debug("fetching notification of user: {}, page: {}, size: {}", receiver.getEmailId(), pageNumber,
				pageSize);
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, Direction.DESC, "createdOn");
		return notificationRepositroy.findByReceiverAndIsDeleted(receiver, false, pageRequest);
	}

	/**
	 * to get all the notification page wise
	 * 
	 * @param pagenumber
	 * @param pagesize
	 * @return page of notifications
	 */
	@Override
	public Page<Notification> getNotification(int pageNumber, int pageSize) {
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, Direction.DESC, "createdOn");
		return notificationRepositroy.findAllNotification(false, pageRequest);
	}

}
