package com.bolenum.services.user;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Base64.Encoder;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bolenum.enums.TwoFactorAuthOption;
import com.bolenum.exceptions.InvalidOtpException;
import com.bolenum.model.OTP;
import com.bolenum.model.User;
import com.bolenum.repo.user.OTPRepository;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.services.common.LocaleService;
import com.bolenum.util.SMSServiceUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

/**
 * 
 * @author Vishal Kumar
 * @date 26-sep
 *
 */
@Service
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {

	final long KEY_VALIDATION_INTERVAL_MS = TimeUnit.SECONDS.toMillis(30);
	int lastUsedPassword = -1; // last successfully used password
	long lastVerifiedTime = 0; // time of last success
	final GoogleAuthenticator gAuth = new GoogleAuthenticator();
	AtomicInteger windowSize = new AtomicInteger(3);
	
	@Value("${bolenum.google.qr.code.location}")
	private String qrCodeLocation;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OTPRepository otpRepository;
	@Autowired
	private SMSServiceUtil smsServiceUtil;
	@Autowired
	private LocaleService localeService;
	
	private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthServiceImpl.class);

	@Override
	public Map<String, String> qrCodeGeneration(User user) throws URISyntaxException, WriterException, IOException {
		String key = getTwoFactorKey(user);
		String filePath = qrCodeLocation + "/" + user.getUserId() + ".png";
		String charset = "UTF-8"; // or "ISO-8859-1"
		Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		String keyUri = generateKeyUri("bolenumexchange.com", "bolenum-exchange", key);

		String qrCodeData = keyUri;
		String base64Image = createQRCode(qrCodeData, filePath, charset, hintMap, 200, 200);
		Map<String, String> map = new HashMap<String, String>();
		map.put("fileName", user.getUserId() + ".png");
		map.put("key", key);
		map.put("base64Image", base64Image);
		return map;
	}

	@Override
	public boolean performAuthentication(String value, User user) throws UsernameNotFoundException {
		Integer totp = Integer.valueOf((value.equals("") ? "-1" : value));
		boolean unused = isUnusedPassword(totp, windowSize.get());
		boolean matches = gAuth.authorize(user.getGoogle2FaAuthKey(), totp);
		if (unused && matches) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public User setTwoFactorAuth(TwoFactorAuthOption twoFactorAuthOption, User user) {
		user.setTwoFactorAuthOption(twoFactorAuthOption);
		return userRepository.save(user);
	}

	@Override
	public OTP sendOtpForTwoFactorAuth(User user) {
		Random r = new Random();
		int code = (100000 + r.nextInt(900000));
		if (user.getIsMobileVerified()) {
			String mobileNumber = user.getMobileNumber();
			String message = localeService.getMessage("otp.for.twofa.verification") + "  " + code;
			logger.debug("2 FA otp sent success: {}", code);
			smsServiceUtil.sendMessage(mobileNumber, message);
			OTP otp = new OTP(mobileNumber, code, user);
			return otpRepository.save(otp);
		} else {
			return null;
		}
	}

	@Override
	public boolean verify2faOtp(int otp) throws InvalidOtpException {
		OTP existingOtp = otpRepository.findByOtp(otp);
		if (existingOtp != null) {
			User user = existingOtp.getUser();
			if (existingOtp.getIsDeleted() == false && existingOtp.getMobileNumber().equals(user.getMobileNumber())) {
				existingOtp.setIsDeleted(true);
				OTP savedOTP = otpRepository.save(existingOtp);
				if (savedOTP != null) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new InvalidOtpException(localeService.getMessage("otp.invalid"));
			}
		} else {
			throw new InvalidOtpException(localeService.getMessage("otp.expired"));
		}
	}

	/**************** private methods *************************/

	/**
	 * 
	 * @param password
	 * @param windowSize
	 * @return Boolean
	 */
	private boolean isUnusedPassword(int password, int windowSize) {
		long now = new Date().getTime();
		long timeslotNow = now / KEY_VALIDATION_INTERVAL_MS;
		long timeslotThen = lastVerifiedTime / KEY_VALIDATION_INTERVAL_MS;

		int forwardTimeslots = ((windowSize - 1) / 2);

		if (password != lastUsedPassword || timeslotNow > timeslotThen + forwardTimeslots) {
			lastUsedPassword = password;
			lastVerifiedTime = now;
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param user
	 * @return String
	 */
	private String getTwoFactorKey(User user) {
		final GoogleAuthenticatorKey googleAuthkey = gAuth.createCredentials();
		String key = googleAuthkey.getKey();
		// User user1 =
		// userRepository.findByEmailIdIgnoreCase(user.getEmailId());
		if (user.getGoogle2FaAuthKey() != null) {
			return user.getGoogle2FaAuthKey();
		} else {
			user.setGoogle2FaAuthKey(key);
			User savedUser = userRepository.save(user);
			return savedUser.getGoogle2FaAuthKey();
		}
	}

	/**
	 * 
	 * @param account
	 * @param issuer
	 * @param secret
	 * @return String
	 * @throws URISyntaxException
	 */
	private static String generateKeyUri(String account, String issuer, String secret) throws URISyntaxException {

		URI uri = new URI("otpauth", "totp", "/" + issuer + ":" + account, "secret=" + secret + "&issuer=" + issuer,
				null);

		return uri.toASCIIString();
	}

	/**
	 * 
	 * @param qrCodeData
	 * @param filePath
	 * @param charset
	 * @param hintMap
	 * @param qrCodeheight
	 * @param qrCodewidth
	 * @return String
	 * @throws WriterException
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private static String createQRCode(String qrCodeData, String filePath, String charset,
			@SuppressWarnings("rawtypes") Map hintMap, int qrCodeheight, int qrCodewidth)
			throws WriterException, IOException {
		@SuppressWarnings("unchecked")
		BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
				BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
		File file = new File(filePath);
		MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), file);
		Encoder encoder = Base64.getEncoder();
		String base64Image = encoder.encodeToString(Files.readAllBytes(file.toPath()));
		logger.debug("QR code file name: {}",String.valueOf(file));
		//using PosixFilePermission to set file permissions 777
        Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
        //add owners permission
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        //perms.add(PosixFilePermission.OWNER_EXECUTE);
        //add group permissions
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        //perms.add(PosixFilePermission.GROUP_EXECUTE);
        //add others permissions
        perms.add(PosixFilePermission.OTHERS_READ);
        //perms.add(PosixFilePermission.OTHERS_WRITE);
        //perms.add(PosixFilePermission.OTHERS_EXECUTE);
        
        Files.setPosixFilePermissions(Paths.get(file.toString()), perms);
        logger.info("2FA QR code generated");
		return "data:image/png;base64," + base64Image;
	}

}
