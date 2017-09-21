package com.bolenum.services.common;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.bolenum.model.User;
import com.bolenum.model.UserKyc;

/**
 * 
 * @author Vishal Kumar
 * @date 19-sep-2017
 *
 */

public interface KYCService {

	/**
	 * 
	 * @param multipartFile
	 * @param userId
	 * @return User
	 * @throws IOException
	 */
	User uploadKycDocument(MultipartFile multipartFile, Long userId) throws IOException;

	/**
	 * 
	 * @param userId
	 * @returnn User
	 */
	User approveKycDocument(Long userId);

	/**
	 * 
	 * @param userId
	 * @param rejectionMessage
	 * @return User
	 */
	User disApprovedKycDocument(Long userId, String rejectionMessage);

	/**
	 * 
	 * @param kycId
	 * @return UserKyc
	 */
	UserKyc getUserKycById(Long kycId);

}
