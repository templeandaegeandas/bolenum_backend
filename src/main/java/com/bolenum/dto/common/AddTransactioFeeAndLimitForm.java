package com.bolenum.dto.common;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @Author Himanshu Kumar
 * @Date 01-Nov-2017
 */
public class AddTransactioFeeAndLimitForm {

	@ApiModelProperty(hidden = true)
	private Long id;

	private Double feeBTC;

	private Double feeOther;

	private Double availableBalanceLimitToWithdrawForBTC;

	private Double availableBalanceLimitToWithdrawForETH;

	private Double availableBalanceLimitToWithdrawForERC20;

	private Double availableBalanceLimitToWithdrawForFIAT;

	private Double minimumLimitToSendForBTC;

	private Double minimumLimitToSendForETH;

	private Double minimumLimitToSendForERC20;

	private Double minimumLimitToSendForFIAT;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the feeBTC
	 */
	public Double getFeeBTC() {
		return feeBTC;
	}

	/**
	 * @param feeBTC the feeBTC to set
	 */
	public void setFeeBTC(Double feeBTC) {
		this.feeBTC = feeBTC;
	}

	/**
	 * @return the feeOther
	 */
	public Double getFeeOther() {
		return feeOther;
	}

	/**
	 * @param feeOther the feeOther to set
	 */
	public void setFeeOther(Double feeOther) {
		this.feeOther = feeOther;
	}

	/**
	 * @return the availableBalanceLimitToWithdrawForBTC
	 */
	public Double getAvailableBalanceLimitToWithdrawForBTC() {
		return availableBalanceLimitToWithdrawForBTC;
	}

	/**
	 * @param availableBalanceLimitToWithdrawForBTC the availableBalanceLimitToWithdrawForBTC to set
	 */
	public void setAvailableBalanceLimitToWithdrawForBTC(Double availableBalanceLimitToWithdrawForBTC) {
		this.availableBalanceLimitToWithdrawForBTC = availableBalanceLimitToWithdrawForBTC;
	}

	/**
	 * @return the availableBalanceLimitToWithdrawForETH
	 */
	public Double getAvailableBalanceLimitToWithdrawForETH() {
		return availableBalanceLimitToWithdrawForETH;
	}

	/**
	 * @param availableBalanceLimitToWithdrawForETH the availableBalanceLimitToWithdrawForETH to set
	 */
	public void setAvailableBalanceLimitToWithdrawForETH(Double availableBalanceLimitToWithdrawForETH) {
		this.availableBalanceLimitToWithdrawForETH = availableBalanceLimitToWithdrawForETH;
	}

	/**
	 * @return the availableBalanceLimitToWithdrawForERC20
	 */
	public Double getAvailableBalanceLimitToWithdrawForERC20() {
		return availableBalanceLimitToWithdrawForERC20;
	}

	/**
	 * @param availableBalanceLimitToWithdrawForERC20 the availableBalanceLimitToWithdrawForERC20 to set
	 */
	public void setAvailableBalanceLimitToWithdrawForERC20(Double availableBalanceLimitToWithdrawForERC20) {
		this.availableBalanceLimitToWithdrawForERC20 = availableBalanceLimitToWithdrawForERC20;
	}

	/**
	 * @return the availableBalanceLimitToWithdrawForFIAT
	 */
	public Double getAvailableBalanceLimitToWithdrawForFIAT() {
		return availableBalanceLimitToWithdrawForFIAT;
	}

	/**
	 * @param availableBalanceLimitToWithdrawForFIAT the availableBalanceLimitToWithdrawForFIAT to set
	 */
	public void setAvailableBalanceLimitToWithdrawForFIAT(Double availableBalanceLimitToWithdrawForFIAT) {
		this.availableBalanceLimitToWithdrawForFIAT = availableBalanceLimitToWithdrawForFIAT;
	}

	/**
	 * @return the minimumLimitToSendForBTC
	 */
	public Double getMinimumLimitToSendForBTC() {
		return minimumLimitToSendForBTC;
	}

	/**
	 * @param minimumLimitToSendForBTC the minimumLimitToSendForBTC to set
	 */
	public void setMinimumLimitToSendForBTC(Double minimumLimitToSendForBTC) {
		this.minimumLimitToSendForBTC = minimumLimitToSendForBTC;
	}

	/**
	 * @return the minimumLimitToSendForETH
	 */
	public Double getMinimumLimitToSendForETH() {
		return minimumLimitToSendForETH;
	}

	/**
	 * @param minimumLimitToSendForETH the minimumLimitToSendForETH to set
	 */
	public void setMinimumLimitToSendForETH(Double minimumLimitToSendForETH) {
		this.minimumLimitToSendForETH = minimumLimitToSendForETH;
	}

	/**
	 * @return the minimumLimitToSendForERC20
	 */
	public Double getMinimumLimitToSendForERC20() {
		return minimumLimitToSendForERC20;
	}

	/**
	 * @param minimumLimitToSendForERC20 the minimumLimitToSendForERC20 to set
	 */
	public void setMinimumLimitToSendForERC20(Double minimumLimitToSendForERC20) {
		this.minimumLimitToSendForERC20 = minimumLimitToSendForERC20;
	}

	/**
	 * @return the minimumLimitToSendForFIAT
	 */
	public Double getMinimumLimitToSendForFIAT() {
		return minimumLimitToSendForFIAT;
	}

	/**
	 * @param minimumLimitToSendForFIAT the minimumLimitToSendForFIAT to set
	 */
	public void setMinimumLimitToSendForFIAT(Double minimumLimitToSendForFIAT) {
		this.minimumLimitToSendForFIAT = minimumLimitToSendForFIAT;
	}

}