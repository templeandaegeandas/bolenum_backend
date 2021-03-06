package com.bolenum.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.web3j.protocol.Web3j;

import com.bolenum.model.User;

/**
 * @author Vishal Kumar
 * @date 14-sep-2017
 */

public class GenericUtils {

	private static Logger logger = LoggerFactory.getLogger(GenericUtils.class);

	private GenericUtils() {

	}

	/**
	 * to get the current logged in user from context
	 * 
	 * @return user
	 */

	public static User getLoggedInUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/**
	 * This method is use to check valid mail
	 * @param email
	 * @return
	 */
	public static boolean isValidMail(String email) {
		String emailPattern = "^(.+)@(.+)$";
		Pattern pattern = Pattern.compile(emailPattern);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * This method is use to convert Wei To Ether
	 * @param
	 * @return amount in either
	 */
	public static Double convertWeiToEther(BigInteger amount) {
		logger.debug("amount in Wei: {}", amount);
		BigDecimal balance = new BigDecimal(amount);
		BigDecimal conversionRate = new BigDecimal(new BigInteger("1000000000000000000"));
		BigDecimal amountInEther = balance.divide(conversionRate);
		logger.debug("amount in eth: {}", getDecimalFormatString(amountInEther.doubleValue()));
		return amountInEther.doubleValue();
	}

	/**
	 * 
	 * @description to calculate the Estimeted Fee of Ethereum
	 * @return estimited fee
	 *
	 */

	public static double getEstimetedFeeEthereum() {
		Web3j web = EthereumServiceUtil.getWeb3jInstance();
		BigInteger gasPrice = BigInteger.ZERO;
		BigInteger gasLimit = new BigInteger("21000");
		BigInteger ptb = BigInteger.ZERO;
		double estimedtedFee = 0.0;
		try {
			gasPrice = web.ethGasPrice().send().getGasPrice();
			ptb = gasPrice.multiply(gasLimit);
			estimedtedFee = GenericUtils.convertWeiToEther(ptb);
			logger.debug("estimedted Fee: {}", getDecimalFormatString(estimedtedFee));
		} catch (IOException e) {
			logger.error("eth fee estimation error: {}", e);
		}
		return estimedtedFee;
	}

	/**
	 * 
	 * @description to get the decimal digit up to 8.
	 * @param amount
	 * @return amount
	 *
	 */
	public static double getDecimalFormat(double amount) {
		DecimalFormat df = new DecimalFormat("0");
		df.setMaximumFractionDigits(8);
		String formate = df.format(amount);
		return Double.parseDouble(formate);
	}

	/**
	 * This method is use to get Decimal Format String
	 * @param amount
	 * @return
	 */
	public static String getDecimalFormatString(Double amount) {
		DecimalFormat df = new DecimalFormat("#0.00000000");
		df.setMaximumFractionDigits(8);
		return df.format(amount);
	}
}