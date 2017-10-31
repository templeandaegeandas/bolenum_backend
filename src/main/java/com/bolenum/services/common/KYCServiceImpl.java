package com.bolenum.services.common;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bolenum.enums.DocumentStatus;
import com.bolenum.enums.DocumentType;
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

	@Autowired
	private KYCService kycService;

	@Value("${bolenum.document.location}")
	private String uploadedFileLocation;

	/**
	 * to upload kyc documents
	 */
	@Override
	public UserKyc uploadKycDocument(MultipartFile file, Long userId, DocumentType documentType)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException {
		long sizeLimit = 1024 * 1024 * 10L;
		User user = userRepository.findOne(userId);
		if (user.getMobileNumber() == null || !user.getIsMobileVerified()) {
			throw new MobileNotVerifiedException(localeService.getMessage("mobile.number.not.verified"));
		}
		UserKyc savedKyc = null;
		if (file != null) {
			String[] validExtentions = { "jpg", "jpeg", "png", "pdf" };
			String updatedFileName = fileUploadService.uploadFile(file, uploadedFileLocation, user, validExtentions,
					sizeLimit);

			List<UserKyc> listOfUserKyc = kycService.getListOfKycByUser(user);

			if (listOfUserKyc == null || listOfUserKyc.size() == 0) {
				UserKyc kyc = new UserKyc();
				kyc.setDocument(updatedFileName);
				kyc.setDocumentType(documentType);
				listOfUserKyc.add(kyc);
				kyc.setUser(user);
				savedKyc = kycRepo.save(kyc);
			} else if (listOfUserKyc.size() > 0 && listOfUserKyc.size() <= 2) {
				// UserKyc userKyc = user.getUserKyc();
				boolean added = false;
				Iterator<UserKyc> iterator = listOfUserKyc.iterator();
				while (iterator.hasNext()) {
					UserKyc userKyc = iterator.next();
					if (userKyc.getDocumentType().equals(documentType)) {
						userKyc.setDocument(updatedFileName);
						userKyc.setDocumentType(documentType);
						userKyc.setIsVerified(false);
						userKyc.setDocumentStatus(DocumentStatus.SUBMITTED);
						userKyc.setVerifiedDate(null);
						userKyc.setRejectionMessage(null);
						added = true;
						savedKyc = kycRepo.save(userKyc);

					}
				}
				if (!added) {
					UserKyc kyc = new UserKyc();
					kyc.setDocument(updatedFileName);
					kyc.setDocumentType(documentType);
					kyc.setUser(user);
					listOfUserKyc.add(kyc);
					savedKyc = kycRepo.save(kyc);
				}
			}
		} else

		{
			return null;
		}
		return savedKyc;
	}

	/**
	 * to approve kyc document
	 */
	@Override
	public UserKyc approveKycDocument(Long kycId) {
		UserKyc userKyc = kycRepo.findOne(kycId);
		userKyc.setVerifiedDate(new Date());
		userKyc.setIsVerified(true);
		userKyc.setDocumentStatus(DocumentStatus.APPROVED);
		userKyc.setRejectionMessage(null);
		User user=userKyc.getUser();
		smsServiceUtil.sendMessage(user.getMobileNumber(), localeService.getMessage("email.text.approve.user.kyc"));
		mailService.mailSend(user.getEmailId(), localeService.getMessage("email.subject.approve.user.kyc"),
				localeService.getMessage("email.text.approve.user.kyc"));
		return userKyc;
	}

	/**
	 * to disapprove kyc document
	 */
	@Override
	public UserKyc disApprovedKycDocument(Long kycId, String rejectionMessage) {

		UserKyc userKyc = kycRepo.findOne(kycId);
		userKyc.setVerifiedDate(null);
		userKyc.setIsVerified(false);
		userKyc.setDocumentStatus(DocumentStatus.DISAPPROVED);
		userKyc.setRejectionMessage(rejectionMessage);
		User user = userKyc.getUser();
		smsServiceUtil.sendMessage(user.getMobileNumber(), localeService.getMessage("email.text.disapprove.user.kyc"));
		mailService.mailSend(user.getEmailId(), localeService.getMessage("email.subject.disapprove.user.kyc"),
				localeService.getMessage("email.text.disapprove.user.kyc"));
		return userKyc;
	}

	// @Override
	// public Page<User> getSubmitedKycList(int pageNumber, int pageSize, String
	// sortBy, String sortOrder,
	// String searchData) {
	// Direction sort;
	// if (sortOrder.equals("desc")) {
	// sort = Direction.DESC;
	// } else {
	// sort = Direction.ASC;
	// }
	// Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
	// return userRepository.getNewlySubmittedKycListWIthSearch(searchData,
	// DocumentStatus.SUBMITTED, pageRequest);
	// }

	@Override
	public UserKyc getUserKycById(Long kycId) {
		return kycRepo.findOne(kycId);
	}

	/**
	 * 
	 */
	@Override
	public DocumentType validateDocumentType(String documentType) {
		for (DocumentType documentTypeToMatch : DocumentType.values()) {
			if (documentTypeToMatch.toString().equals(documentType)) {
				return documentTypeToMatch;
			}
		}
		return null;
	}

	@Override
	public Page<UserKyc> getListOfKyc(int pageNumber, int pageSize, String sortBy, String sortOrder,
			String searchData) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		} else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		return kycRepo.findByDocumentStatus(DocumentStatus.SUBMITTED, pageRequest);

	}

	@Override
	public List<UserKyc> getListOfKycByUser(User user) {
		return kycRepo.findByUser(user);
	}

	


}
