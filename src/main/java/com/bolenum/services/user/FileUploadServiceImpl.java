package com.bolenum.services.user;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

	@Override
	public String uploadFile(MultipartFile multipartFile, String storageLocation, User user, String[] validExtentions,
			long maxSize) throws IOException, PersistenceException, MaxSizeExceedException {
		if (multipartFile.getSize() > maxSize) {
			throw new MaxSizeExceedException(localeService.getMessage("max.file.size.exceeds"));
		}
		String originalFileName = multipartFile.getOriginalFilename();
		int dot = originalFileName.lastIndexOf('.');
		String extension = (dot == -1) ? "" : originalFileName.substring(dot + 1);
		if (!Arrays.asList(validExtentions).contains(extension.toLowerCase())) {
			throw new PersistenceException(localeService.getMessage("valid.image.extention.error"));
		}
		String updatedFileName = user.getFirstName() + "_" + user.getUserId() + "." + extension;
		InputStream inputStream = multipartFile.getInputStream();
		BufferedImage imageFromConvert = ImageIO.read(inputStream);
		File userKycFile = new File(storageLocation + updatedFileName);
		ImageIO.write(imageFromConvert, extension, userKycFile);
		return updatedFileName;
	}
}
