package com.bolenum.services.user;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.User;

/**
 * 
 * @author Vishal Kumar
 * @date 22-sep-2017
 *
 */
public interface FileUploadService {

	/**
	 * 
	 * @param multipartFile
	 * @param storageLocation
	 * @param user
	 * @param validExtentions
	 * @param maxSize
	 * @return String file name
	 * @throws IOException
	 * @throws PersistenceException
	 * @throws MaxSizeExceedException
	 */
	String uploadFile(MultipartFile multipartFile, String storageLocation, User user, String[] validExtentions,
			long maxSize) throws IOException, PersistenceException, MaxSizeExceedException;

}