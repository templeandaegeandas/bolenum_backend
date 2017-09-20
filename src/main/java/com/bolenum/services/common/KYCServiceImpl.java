package com.bolenum.services.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bolenum.constant.DocumentStatus;
import com.bolenum.model.User;
import com.bolenum.model.UserKyc;
import com.bolenum.repo.common.KYCRepo;
import com.bolenum.repo.user.UserRepository;

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
	
	@Value("${bolenum.document.location}")
	private String uploadedFileLocation;

	@Override
	public User uploadKycDocument(MultipartFile multipartFile, Long userId) throws IOException {
		User user = userRepository.findOne(userId);
		if (multipartFile != null) {
			String originalFileName = multipartFile.getOriginalFilename();
			int dot = originalFileName.lastIndexOf(".");
			String extension = (dot == -1) ? "" : originalFileName.substring(dot + 1);
			String updatedFileName = user.getFirstName() + "_" + userId + "." + extension;
			InputStream inputStream = multipartFile.getInputStream();
			BufferedImage ImageFromConvert = ImageIO.read(inputStream);
			File userKycFile = new File(uploadedFileLocation + updatedFileName);
			ImageIO.write(ImageFromConvert, extension, userKycFile);
			if (user.getUserKyc() == null) {
				UserKyc kyc = new UserKyc();
				kyc.setDocument(updatedFileName);
				kyc = kycRepo.save(kyc);
				user.setUserKyc(kyc);
			} else {
				UserKyc userKyc = user.getUserKyc();
				userKyc.setDocument(updatedFileName);
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
		return userRepository.save(user);
	}
	
	@Override
	public User disApprovedKycDocument(Long userId, String rejectionMessage) {
		User user = userRepository.findOne(userId);
		UserKyc userKyc = user.getUserKyc();
		userKyc.setVerifiedDate(null);
		userKyc.setIsVerified(false);
		userKyc.setDocumentStatus(DocumentStatus.DISAPPROVED);
		userKyc.setRejectionMessage(rejectionMessage);
		user.setUserKyc(userKyc);
		return userRepository.save(user);
	}
	
}
