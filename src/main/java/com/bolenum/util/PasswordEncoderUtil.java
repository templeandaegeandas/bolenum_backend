/**
 * 
 */
package com.bolenum.util;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author chandan kumar singh
 * @date 12-Sep-2017
 */
@Service
public class PasswordEncoderUtil {
	private static BCryptPasswordEncoder passwordEncoder = null;

	@Value("${bolenum.tokensecret}")
	private String secret;

	/**
	 * This method is use for String encode
	 * @param rawPassword
	 * @return
	 */
	public String encode(CharSequence rawPassword) {
		if (passwordEncoder == null)
			create();
		return passwordEncoder.encode(rawPassword);
	}

	/**
	 * This method is use for matches
	 * @param rawPassword
	 * @param encodedPassword
	 * @return
	 */
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (passwordEncoder == null)
			create();
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	private void create() {
		passwordEncoder = new BCryptPasswordEncoder(4, new SecureRandom(secret.getBytes()));
	}
}
