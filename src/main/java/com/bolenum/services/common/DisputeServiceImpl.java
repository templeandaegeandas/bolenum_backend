package com.bolenum.services.common;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.bolenum.repo.user.UserRepository;
import com.bolenum.services.user.FileUploadService;

@Service
public class DisputeServiceImpl implements DisputeService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DisputeOrderRepo disputeOrderRepo;

	@Autowired
	private FileUploadService fileUploadService;

	@Autowired
	private OrdersRepository ordersRepository;

	@Value("${bolenum.document.location}")
	private String uploadedFileLocation;

	public DisputeServiceImpl() {

	}

	/**
	 * 
	 */
	@Override
	public DisputeOrder uploadProofDocument(MultipartFile file, Long disputeId, Long userId)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException {
		long sizeLimit = 1024 * 1024 * 10L;
		User user = userRepository.findOne(userId);
		DisputeOrder disputeOrder = disputeOrderRepo.findOne(disputeId);
		DisputeOrder savedDispute = null;
		if (file != null) {
			String[] validExtentions = { "jpg", "jpeg", "png", "pdf" };
			String updatedFileName = fileUploadService.uploadFile(file, uploadedFileLocation, user, null,
					validExtentions, sizeLimit);
			disputeOrder.setFirstdocumenForProofToDispute(updatedFileName);
			savedDispute = disputeOrderRepo.saveAndFlush(disputeOrder);
			return savedDispute;

		} else {
			return null;
		}

	}

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

	@Override
	public DisputeOrder raiseDispute(Long orderId, Long transactionId, String comment) {
		Orders order = ordersRepository.findOne(orderId);
		if (order != null && OrderStatus.LOCKED.equals(order.getOrderStatus())
				&& OrderType.BUY.equals(order.getOrderType())) {
			User disputeRaiser = order.getUser();
			DisputeOrder disputeOrder = new DisputeOrder();
			disputeOrder.setDisputeRaiser(disputeRaiser);
			disputeOrder.setDisputeStatus(DisputeStatus.RAISED);
			disputeOrder.setComment(comment);
			disputeOrder.setTransactionId(transactionId);

			return disputeOrderRepo.saveAndFlush(disputeOrder);

		}
		return null;
	}

	@Override
	public Boolean isAlreadyDisputed(Long orderId, Long transactionId) {
		DisputeOrder disputeOrder = disputeOrderRepo.findByOrderIdAndTransactionId(orderId, transactionId);
		if (disputeOrder == null) {
			return true;
		}
		return false;
	}

	@Override
	public Boolean checkEligibilityToDispute(Long orderId) {
		Orders order = ordersRepository.findOne(orderId);
		if (order.getOrderType().equals(OrderType.BUY)) {
			return true;
		}
		return false;
	}

}
