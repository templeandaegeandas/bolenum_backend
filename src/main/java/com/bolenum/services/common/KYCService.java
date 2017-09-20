package com.bolenum.services.common;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.bolenum.model.User;

/**
 * 
 * @author Vishal Kumar
 * @date 19-sep-2017
 *
 */

public interface KYCService {

	User uploadKycDocument(MultipartFile multipartFile, Long userId) throws IOException;

	User approveKycDocument(Long userId);

	User disApprovedKycDocument(Long userId, String rejectionMessage);

}
