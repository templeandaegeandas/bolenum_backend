package com.bolenum.services.common;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bolenum.enums.DisputeStatus;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.MobileNotVerifiedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.DisputeOrder;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.repo.order.book.DisputeOrderRepo;
import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.services.user.FileUploadService;
import com.bolenum.services.user.notification.NotificationService;

/**
 * 
 * @author Himanshu Kumar
 *
 */

@Service
public class DisputeServiceImpl implements DisputeService {

	@Autowired
	private DisputeOrderRepo disputeOrderRepo;

	@Autowired
	private FileUploadService fileUploadService;

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private NotificationService notificationService;

	@Value("${bolenum.document.location}")
	private String uploadedFileLocation;

	public DisputeServiceImpl() {

	}

	/**
	 * 
	 */
	@Override
	public DisputeOrder uploadProofDocument(MultipartFile file, DisputeOrder disputeOrder, User disputeRaiser)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException {

		long sizeLimit = 1024 * 1024 * 10L;
		DisputeOrder savedDispute = null;
		if (file != null) {
			String[] validExtentions = { "jpg", "jpeg", "png", "pdf" };
			String updatedFileName = fileUploadService.uploadFile(file, uploadedFileLocation, disputeRaiser, null,
					validExtentions, sizeLimit);
			disputeOrder.setFirstDocumenForProofToDispute(updatedFileName);
			savedDispute = disputeOrderRepo.saveAndFlush(disputeOrder);
			return savedDispute;

		} else {
			return null;
		}

	}

	/**
	 * 
	 */
	@Override
	public Boolean checkExpiryToDispute(Long orderId) {
		Orders order = ordersRepository.findOne(orderId);

		Date previous = order.getCreatedOn();
		Date now = new Date();
		if (now.getTime() - previous.getTime() <= 15 * 60 * 1000) {
			return false;

		}
		return true;
	}

	/**
	 * 
	 */
	@Override
	public DisputeOrder raiseDispute(Long orderId, Long transactionId, String commentByDisputeRaiser,
			MultipartFile file)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException {

		Orders order = ordersRepository.findOne(orderId);

		Orders matchedOrder = order.getMatchedOrder();

		User disputeRaisedAgainst = matchedOrder.getUser();

		if (order != null && OrderStatus.LOCKED.equals(order.getOrderStatus())
				&& OrderType.BUY.equals(order.getOrderType())) {

			User disputeRaiser = order.getUser();
			DisputeOrder disputeOrder = new DisputeOrder();
			disputeOrder.setDisputeRaiser(disputeRaiser);
			disputeOrder.setDisputeStatus(DisputeStatus.RAISED);
			disputeOrder.setCommentByDisputeRaiser(commentByDisputeRaiser);
			disputeOrder.setTransactionId(transactionId);
			disputeOrder.setDisputeRaisedAgainst(disputeRaisedAgainst);
			DisputeOrder response = uploadProofDocument(file, disputeOrder, disputeRaiser);
			return response;
			// return disputeOrderRepo.saveAndFlush(disputeOrder);

		}
		return null;
	}

	/**
	 * 
	 */
	@Override
	public Boolean isAlreadyDisputed(Long orderId, Long transactionId) {
		DisputeOrder disputeOrder = disputeOrderRepo.findByOrderIdOrTransactionId(orderId, transactionId);
		if (disputeOrder == null) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public Boolean checkEligibilityToDispute(Long orderId) {
		Orders order = ordersRepository.findOne(orderId);
		if (OrderType.BUY.equals(order.getOrderType())) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public Page<DisputeOrder> getListOfDisputeOrder(int pageNumber, int pageSize, String sortBy, String sortOrder,
			DisputeStatus disputeStatus) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);

		return disputeOrderRepo.findByDisputeStatus(disputeStatus, pageRequest);
	}

	/**
	 * 
	 */
	@Override
	public DisputeOrder getDisputeOrderByID(Long disputeId) {
		return disputeOrderRepo.findOne(disputeId);
	}

	/**
	 * 
	 */
	@Override
	public DisputeOrder performActionOnRaisedDispute(DisputeOrder disputeOrder, String commentForDisputeRaiser,
			String commentForDisputeRaisedAgainst, DisputeStatus disputeStatus) {

		if (DisputeStatus.RAISED.equals(disputeOrder.getDisputeStatus())
				&& disputeStatus.equals(DisputeStatus.INPROCESS)) {
			disputeOrder.setDisputeStatus(disputeStatus);
			disputeOrder.setCommentForDisputeRaiser(commentForDisputeRaiser);
			disputeOrder.setCommentForDisputeRaisedAgainst(commentForDisputeRaisedAgainst);

		} else if (DisputeStatus.INPROCESS.equals(disputeOrder.getDisputeStatus())
				&& disputeStatus.equals(DisputeStatus.COMPLETED)) {
			disputeOrder.setDisputeStatus(disputeStatus);
			disputeOrder.setCommentForDisputeRaiser(commentForDisputeRaiser);
			disputeOrder.setCommentForDisputeRaisedAgainst(commentForDisputeRaisedAgainst);
		}
		return disputeOrderRepo.save(disputeOrder);
	}

	/**
	 * used to send dispute notification to buyer and seller with admin comment
	 */
	@Override
	public void sendDisputeNotification(DisputeOrder disputeOrder, User disputeRaiser, User disputeRaisedAgainst) {

		String messageForDisputeRaiser = "hi , " + "  " + disputeRaiser.getFirstName() + '\n'
				+ "your have requested to dispute your order against " + disputeRaisedAgainst.getFirstName()
				+ ".your order id is" + disputeOrder.getOrderId() + "and your dispute is "
				+ disputeOrder.getDisputeStatus() + disputeOrder.getCommentForDisputeRaiser();

		notificationService.sendNotificationForDispute(disputeRaiser, messageForDisputeRaiser);

		String messageForDisputeRaisedAgainst = "hi , " + disputeRaisedAgainst.getFirstName() + '\n'
				+ disputeRaiser.getFirstName() + "has raised dispute against you for order id "
				+ disputeOrder.getOrderId() + disputeOrder.getCommentForDisputeRaisedAgainst();

		notificationService.sendNotificationForDispute(disputeRaisedAgainst, messageForDisputeRaisedAgainst);

		notificationService.saveNotification(disputeRaiser, disputeRaisedAgainst, disputeOrder.getReason());

	}

}
