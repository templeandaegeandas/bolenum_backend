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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getFeeBTC() {
		return feeBTC;
	}

	public void setFeeBTC(Double feeBTC) {
		this.feeBTC = feeBTC;
	}

	public Double getFeeOther() {
		return feeOther;
	}

	public void setFeeOther(Double feeOther) {
		this.feeOther = feeOther;
	}

	public Double getAvailableBalanceLimitToWithdrawForBTC() {
		return availableBalanceLimitToWithdrawForBTC;
	}

	public void setAvailableBalanceLimitToWithdrawForBTC(Double availableBalanceLimitToWithdrawForBTC) {
		this.availableBalanceLimitToWithdrawForBTC = availableBalanceLimitToWithdrawForBTC;
	}

	public Double getAvailableBalanceLimitToWithdrawForETH() {
		return availableBalanceLimitToWithdrawForETH;
	}

	public void setAvailableBalanceLimitToWithdrawForETH(Double availableBalanceLimitToWithdrawForETH) {
		this.availableBalanceLimitToWithdrawForETH = availableBalanceLimitToWithdrawForETH;
	}

	public Double getAvailableBalanceLimitToWithdrawForERC20() {
		return availableBalanceLimitToWithdrawForERC20;
	}

	public void setAvailableBalanceLimitToWithdrawForERC20(Double availableBalanceLimitToWithdrawForERC20) {
		this.availableBalanceLimitToWithdrawForERC20 = availableBalanceLimitToWithdrawForERC20;
	}

	public Double getAvailableBalanceLimitToWithdrawForFIAT() {
		return availableBalanceLimitToWithdrawForFIAT;
	}

	public void setAvailableBalanceLimitToWithdrawForFIAT(Double availableBalanceLimitToWithdrawForFIAT) {
		this.availableBalanceLimitToWithdrawForFIAT = availableBalanceLimitToWithdrawForFIAT;
	}

	public Double getMinimumLimitToSendForBTC() {
		return minimumLimitToSendForBTC;
	}

	public void setMinimumLimitToSendForBTC(Double minimumLimitToSendForBTC) {
		this.minimumLimitToSendForBTC = minimumLimitToSendForBTC;
	}

	public Double getMinimumLimitToSendForETH() {
		return minimumLimitToSendForETH;
	}

	public void setMinimumLimitToSendForETH(Double minimumLimitToSendForETH) {
		this.minimumLimitToSendForETH = minimumLimitToSendForETH;
	}

	public Double getMinimumLimitToSendForERC20() {
		return minimumLimitToSendForERC20;
	}

	public void setMinimumLimitToSendForERC20(Double minimumLimitToSendForERC20) {
		this.minimumLimitToSendForERC20 = minimumLimitToSendForERC20;
	}

	public Double getMinimumLimitToSendForFIAT() {
		return minimumLimitToSendForFIAT;
	}

	public void setMinimumLimitToSendForFIAT(Double minimumLimitToSendForFIAT) {
		this.minimumLimitToSendForFIAT = minimumLimitToSendForFIAT;
	}

}
