/**
 * 
 */
package com.bolenum.services.user.notification;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bolenum.model.User;
import com.bolenum.model.notification.Notification;
import com.bolenum.repo.user.notification.NotificationRepositroy;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.MailService;

/**
 * @author chandan kumar singh
 * @date 31-Oct-2017
 */
@Service
public class NotificationServiceImpl implements NotificationService {
	private Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

	@Autowired
	private MailService mailService;

	@Autowired
	private NotificationRepositroy notificationRepositroy;

	@Autowired
	private LocaleService localeService;

	/**
	 * to send the notification
	 */
	@Override
	public boolean sendNotification(User user, String message) {
		Future<Boolean> status = mailService.mailSend(user.getEmailId(), localeService.getMessage("trade.summary"),
				message);
		if (status.isDone()) {
			logger.debug("notification send to : {}", user.getEmailId());
		}
		return false;
	}

	/**
	 * to send dispute notification with respect to buyer/seller
	 */
	@Override
	public boolean sendNotificationForDispute(User user, String message) {
		Future<Boolean> status = mailService.mailSend(user.getEmailId(), localeService.getMessage("dispute.summary"),
				message);
		if (status.isDone()) {
			logger.debug("notification send to : {}", user.getEmailId());
		}
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
	 * 
	 */
	@Async
	@Override
	public Notification saveNotification(User sender, User receiver, String msg) {

		Notification notification = new Notification();
		notification.setSender(sender);
		notification.setReceiver(receiver);
		notification.setMessage(msg);
		notification.setReadStatus(false);
		notification.setDeleted(false);
		return notificationRepositroy.save(notification);

	}

	/**
	 * @Created by Himanshu Kumar
	 * 
	 */
	@Override
	public Page<Notification> getListOfNotification(User admin, int pageNumber, int pageSize, String sortOrder,
			String sortBy) {

		Date endDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.DATE, -1);
		Date startDate = c.getTime();

		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}

		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return notificationRepositroy.findByReceiverAndCreatedOnBetween(admin, startDate, endDate, false, pageRequest);
	}

	/**
	 * @Created by Himanshu Kumar
	 */
	@Override
	public Notification getRequestedNotification(Long id) {
		return notificationRepositroy.findOne(id);
	}

	/**
	 * @Created by Himanshu Kumar
	 */
	@Override
	public void setActionOnNotifiction(Long id) {
		Notification notifictaion=notificationRepositroy.findOne(id);
		notifictaion.setReadStatus(true);
		notificationRepositroy.save(notifictaion);
	}

	@Override
	public Long countUnSeenNotification(User user) {
		return notificationRepositroy.countNotificationByReceiverAndReadStatus(user, false);

	}

	@Override
	public void changeNotificationsStatus(Long[] arrayOfNotification) {
		for (int i=0;i<arrayOfNotification.length;i++) {
			Long id = arrayOfNotification[i];
			setActionOnNotifiction(id);
		
		}		
	}

}
