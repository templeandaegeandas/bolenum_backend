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

	public Page<Notification> findBySenderAndIsDeleted(User sender, boolean isDeleted, Pageable pagable);

	public Page<Notification> findByReceiverAndIsDeleted(User Receiver, boolean isDeleted, Pageable pagable);

	@Query("select n from Notification n where (n.receiver = :user and n.createdOn <= :endDate and n.createdOn >= :startDate) or (n.receiver = :user and n.readStatus = :readStatus) order by n.readStatus asc , n.createdOn desc")
	public Page<Notification> findByReceiverAndCreatedOnBetween(@Param("user") User user,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("readStatus") boolean readStatus,
			Pageable pageRequest);

	public Long countByReceiverAndReadStatus(User user, boolean readStatus);

	public Page<Notification> findByCreatedOnBetween(Date startDate, Date endDate, Pageable pageRequest);

}