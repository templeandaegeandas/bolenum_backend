/*@Description Of Class
 * 
 * FileUploadServiceImpl class is responsible for below listed task: 
 *   
 *     Upload file
 *     Update user image
 *    
 **/
package com.bolenum.services.user;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bolenum.enums.DocumentType;
import com.bolenum.exceptions.MaxSizeExceedException;
import com.bolenum.exceptions.PersistenceException;
import com.bolenum.model.User;
import com.bolenum.services.common.LocaleService;

/**
 * 
 * @Author Vishal Kumar
 * @Date 25-Sep-2017
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

	@Autowired
	private LocaleService localeService;

	private static final Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);
	
	
	/**@Description Use to upload file 
	 * @param       multipartFile
	 * @param       storageLocation
	 * @param       user
	 * @param       documentType
	 * @param       validExtentions
	 * @param       maxSize
	 * @return      updated File Names
	 */
	@Override
	public String uploadFile(MultipartFile multipartFile, String storageLocation, User user, DocumentType documentType,
			String[] validExtentions, long maxSize) throws IOException, PersistenceException, MaxSizeExceedException {
		if (multipartFile.getSize() > maxSize) {
			throw new MaxSizeExceedException(localeService.getMessage("max.file.size.exceeds"));
		}
		String originalFileName = multipartFile.getOriginalFilename();
		int dot = originalFileName.lastIndexOf('.');
		String extension = (dot == -1) ? "" : originalFileName.substring(dot + 1);
		if (!Arrays.asList(validExtentions).contains(extension.toLowerCase())) {
			throw new PersistenceException(localeService.getMessage("valid.image.extention.error"));
		}

		String updatedFileName = "";
		if (documentType == null) {
			updatedFileName = user.getUserId() + "_dispute" + "." + extension;
		} else {
			updatedFileName = documentType + "_" + user.getUserId() + "." + extension;
		}
		byte[] buf = new byte[1024];
		File file = new File(storageLocation + updatedFileName);
		try (InputStream inputStream = multipartFile.getInputStream();
				FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			int numRead = 0;
			while ((numRead = inputStream.read(buf)) >= 0) {
				fileOutputStream.write(buf, 0, numRead);
			}
		}
		// using PosixFilePermission to set file permissions 777
		Set<PosixFilePermission> perms = new HashSet<>();
		// add owners permission
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		// add others permissions
		perms.add(PosixFilePermission.OTHERS_READ);

		Files.setPosixFilePermissions(Paths.get(file.toString()), perms);
		return updatedFileName;
	}
	/**@Description Use to update user image 
	 * @param       imageBase64
	 * @param       storageLocation
	 * @param       user
	 * @param       maxSize
	 * @throws      IOException
	 * @throws      MaxSizeExceedException
	 * @throws      PersistenceException
	 * @return      updated File Names
	 */
	@Override
	public String updateUserImage(String imageBase64, String storageLocation, User user, String[] validExtentions,
			long maxSize) throws IOException, MaxSizeExceedException, PersistenceException {
		logger.debug("Profile pic size: {}", imageBase64.length());
		logger.debug("Allowed maximum size: {}", maxSize);

		if (imageBase64.length() > maxSize) {
			throw new MaxSizeExceedException(localeService.getMessage("max.file.size.exceeds"));
		}
		String partSeparator = ",";
		if (imageBase64.contains(partSeparator)) {
			String encodedImg = imageBase64.split(partSeparator)[1];
			String extension = imageBase64.split(partSeparator)[0].split(";")[0].split("/")[1];
			if (!Arrays.asList(validExtentions).contains(extension.toLowerCase())) {
				throw new PersistenceException(localeService.getMessage("valid.image.extention.error"));
			}
			byte[] decodedImg = Base64.getDecoder().decode(encodedImg.getBytes(StandardCharsets.UTF_8));
			String updatedFileName;
			File file;
			try (InputStream in = new ByteArrayInputStream(decodedImg)) {
				updatedFileName = user.getFirstName() + "_" + user.getUserId() + "." + extension;
				BufferedImage imageFromConvert = ImageIO.read(in);
				file = new File(storageLocation + updatedFileName);
				ImageIO.write(imageFromConvert, extension, file);
			}
			// using PosixFilePermission to set file permissions 777
			Set<PosixFilePermission> perms = new HashSet<>();
			// add owners permission
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);
			// add group permissions
			perms.add(PosixFilePermission.GROUP_READ);
			perms.add(PosixFilePermission.GROUP_WRITE);
			// add others permissions
			perms.add(PosixFilePermission.OTHERS_READ);
			Files.setPosixFilePermissions(Paths.get(file.toString()), perms);
			logger.info("profile pic uploaded success");
			return updatedFileName;
		}
		return null;
	}
}
