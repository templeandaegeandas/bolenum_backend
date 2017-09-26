package com.bolenum.services.common;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.MobileNotVerifiedException;
import com.bolenum.exceptions.PersistenceException;
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
	 * @throws MaxSizeExceedException
	 */
	User uploadKycDocument(MultipartFile multipartFile, Long userId)
			throws IOException, PersistenceException, MaxSizeExceedException, MobileNotVerifiedException;

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
	 * @param pageNumber
	 * @param pageSize
	 * @return Page<User>
	 */
	Page<User> getSubmitedKycList(int pageNumber, int pageSize, String sortBy, String sortOrder, String searchData);

	/**
	 * 
	 * @param kycId
	 * @return UserKyc
	 */
	UserKyc getUserKycById(Long kycId);

}
