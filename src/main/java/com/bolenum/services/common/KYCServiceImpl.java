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

import com.bolenum.constant.DocumentStatus;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.MobileNotVerifiedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.User;
import com.bolenum.model.UserKyc;
import com.bolenum.repo.common.KYCRepo;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.services.user.FileUploadService;
import com.bolenum.util.MailService;
import com.bolenum.util.SMSServiceUtil;

/**
 * 
 * @author Vishal Kumar
 * @date 19-sep-2017
 *
 */

@Service
public class KYCServiceImpl implements KYCService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private KYCRepo kycRepo;

	@Autowired
	private FileUploadService fileUploadService;

	@Autowired
	private MailService mailService;

	@Autowired
	private LocaleService localeService;
	@Autowired
	private SMSServiceUtil smsServiceUtil;

	@Value("${bolenum.document.location}")
	private String uploadedFileLocation;

	@Override
	public User uploadKycDocument(MultipartFile file, Long userId)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException {
		long sizeLimit = 1024 * 1024 * 10L;
		User user = userRepository.findOne(userId);
		if (user.getMobileNumber() == null || !user.getIsMobileVerified()) {
			throw new MobileNotVerifiedException(localeService.getMessage("mobile.number.not.verified"));
		}
		if (file != null) {
			String[] validExtentions = { "jpg", "jpeg", "png", "pdf" };
			String updatedFileName = fileUploadService.uploadFile(file, uploadedFileLocation, user, validExtentions,
					sizeLimit);
			if (user.getUserKyc() == null) {
				UserKyc kyc = new UserKyc();
				kyc.setDocument(updatedFileName);
				kyc = kycRepo.save(kyc);
				user.setUserKyc(kyc);
			} else {
				UserKyc userKyc = user.getUserKyc();
				userKyc.setDocument(updatedFileName);
				userKyc.setIsVerified(false);
				userKyc.setDocumentStatus(DocumentStatus.SUBMITTED);
				userKyc.setVerifiedDate(null);
				userKyc.setRejectionMessage(null);
				userKyc = kycRepo.save(userKyc);
				user.setUserKyc(userKyc);
			}
			return userRepository.save(user);
		} else {
			return null;
		}
	}

	@Override
	public User approveKycDocument(Long userId) {
		User user = userRepository.findOne(userId);
		UserKyc userKyc = user.getUserKyc();
		userKyc.setVerifiedDate(new Date());
		userKyc.setIsVerified(true);
		userKyc.setDocumentStatus(DocumentStatus.APPROVED);
		userKyc.setRejectionMessage(null);
		user.setUserKyc(userKyc);
		user = userRepository.save(user);
		smsServiceUtil.sendMessage(user.getMobileNumber(), localeService.getMessage("email.text.approve.user.kyc"));
		mailService.mailSend(user.getEmailId(), null,localeService.getMessage("email.subject.approve.user.kyc"), localeService.getMessage("email.text.approve.user.kyc"));
		mailService.mailSend(user.getEmailId(), null, localeService.getMessage("email.subject.approve.user.kyc"),
				localeService.getMessage("email.text.approve.user.kyc"));
		return user;
	}

	@Override
	public User disApprovedKycDocument(Long userId, String rejectionMessage) {
		User user = userRepository.findOne(userId);
		UserKyc userKyc = user.getUserKyc();
		userKyc.setVerifiedDate(null);
		userKyc.setIsVerified(false);
		userKyc.setDocumentStatus(DocumentStatus.DISAPPROVED);
		userKyc.setRejectionMessage(rejectionMessage);
		user = userRepository.save(user);
		smsServiceUtil.sendMessage(user.getMobileNumber(), localeService.getMessage("email.text.disapprove.user.kyc"));
		mailService.mailSend(user.getEmailId(), null,localeService.getMessage("email.subject.disapprove.user.kyc"), localeService.getMessage("email.text.disapprove.user.kyc"));
		mailService.mailSend(user.getEmailId(), null, localeService.getMessage("email.subject.disapprove.user.kyc"),
				localeService.getMessage("email.text.disapprove.user.kyc"));
		return user;
	}

	@Override
	public Page<User> getSubmitedKycList(int pageNumber, int pageSize, String sortBy, String sortOrder,
			String searchData) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return userRepository.getNewlySubmittedKycListWIthSearch(searchData, DocumentStatus.SUBMITTED, pageRequest);
	}

	@Override
	public UserKyc getUserKycById(Long kycId) {
		return kycRepo.findOne(kycId);
	}

}
