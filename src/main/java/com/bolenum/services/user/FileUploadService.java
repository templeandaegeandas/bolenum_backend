/*@Description Of interface
 * 
 * FileUploadService interface is responsible for below listed task: 
 *   
 *     upload file
 *     update user image
 *   
 */


package com.bolenum.services.user;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.bolenum.enums.DocumentType;
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
	 * @description use to upload file
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
	String uploadFile(MultipartFile multipartFile, String storageLocation, User user, DocumentType documentType, String[] validExtentions,
			long maxSize) throws IOException, PersistenceException, MaxSizeExceedException;

	/**
	 * @description use to update user image.
	 * @param imageBase64
	 * @param storageLocation
	 * @param user
	 * @param validExtentions
	 * @param maxSize
	 * @return String file Name
	 * @throws IOException
	 * @throws MaxSizeExceedException
	 */
	String updateUserImage(String imageBase64, String storageLocation, User user, String[] validExtentions,
			long maxSize) throws IOException, MaxSizeExceedException, PersistenceException;

}
