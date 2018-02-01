/**
 * 
 */
package com.bolenum.repo.user.notification;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bolenum.model.User;
import com.bolenum.model.notification.Notification;

/**
 * @author chandan kumar singh
 * 
 * @modified Himanshu Kumar
 * 
 * @date 31-Oct-2017
 */

public interface NotificationRepositroy extends JpaRepository<Notification, Serializable> {

	/**
	 * This method is use to find By Sender And Is Deleted
	 * @param sender
	 * @param isDeleted
	 * @param pagable
	 * @return
	 */
	public Page<Notification> findBySenderAndIsDeleted(User sender, boolean isDeleted, Pageable pagable);

	/**
	 * This method is use to find By Receiver And Is Deleted
	 * @param Receiver
	 * @param isDeleted
	 * @param pagable
	 * @return
	 */
	public Page<Notification> findByReceiverAndIsDeleted(User Receiver, boolean isDeleted, Pageable pagable);

	/**
	 * This method is use to find By Receiver And Created On Between
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @param readStatus
	 * @param pageRequest
	 * @return
	 */
	@Query("select n from Notification n where (n.receiver = :user and n.createdOn <= :endDate and n.createdOn >= :startDate) or (n.receiver = :user and n.readStatus = :readStatus) order by n.readStatus asc , n.createdOn desc")
	public Page<Notification> findByReceiverAndCreatedOnBetween(@Param("user") User user,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("readStatus") boolean readStatus,
			Pageable pageRequest);

	/**
	 * This method is use to count By Receiver And Read Status
	 * @param user
	 * @param readStatus
	 * @return
	 */
	public Long countByReceiverAndReadStatus(User user, boolean readStatus);

	/**
	 * This method is use to find By Created On Between
	 * @param startDate
	 * @param endDate
	 * @param pageRequest
	 * @return
	 */
	public Page<Notification> findByCreatedOnBetween(Date startDate, Date endDate, Pageable pageRequest);

}