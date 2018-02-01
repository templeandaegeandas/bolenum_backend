/**
 * 
 */
package com.bolenum.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chandan kumar singh
 * @date 26-Sep-2017
 */
public class CryptoUtil {
	
	public static final String AES = "AES";
	
	private static final Logger logger = LoggerFactory.getLogger(CryptoUtil.class);

	private CryptoUtil() {

	}

	/**
	 * This method is use to get Secret Key Spec 
	 * @param myKey
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	private static SecretKeySpec getSecretKeySpec(String myKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] key = myKey.getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16);
		return new SecretKeySpec(key, AES);
	}

	
	
    /**
     * This method is use for byte Array To Hex String
     * @param b
     * @return
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

//    private static byte[] hexStringToByteArray(String s) {
//        byte[] b = new byte[s.length() / 2];
//        for (int i = 0; i < b.length; i++) {
//            int index = i * 2;
//            int v = Integer.parseInt(s.substring(index, index + 2), 16);
//            b[i] = (byte) v;
//        }
//        return b;
//    }

    /**
     * This method is use to get Secret Key
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(128);
        SecretKey sk = keyGen.generateKey();
        String key = byteArrayToHexString(sk.getEncoded());
        logger.debug("key: {}", key);
        return key;
    }
    
	
	
	
	/**
	 * This method is for encrypt
	 * @param strToEncrypt
	 * @param secret
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String encrypt(String strToEncrypt, String secret)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		SecretKeySpec secretKeySpec = getSecretKeySpec(secret);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
	}

	/**
	 * This method is use for decrypt
	 * @param strToDecrypt
	 * @param secret
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String decrypt(String strToDecrypt, String secret)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		SecretKeySpec secretKeySpec = getSecretKeySpec(secret);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
	}
}
