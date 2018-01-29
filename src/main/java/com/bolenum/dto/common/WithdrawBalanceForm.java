package com.bolenum.dto.common;

import org.hibernate.validator.constraints.NotBlank;


/**
 * 
 * @Author Himanshu
 * @Date 25-Oct-2017
 * 
 */
public class WithdrawBalanceForm {


	//@Pattern(regexp = "\\d+(\\.\\d{2})?|\\.\\d{5}", message = "Please enter valid ammount after decimal enter upto 5 digits")
	private Double withdrawAmount;

	@NotBlank
	private String toAddress;

	/**
	 * @return the withdrawAmount
	 */
	public Double getWithdrawAmount() {
		return withdrawAmount;
	}

	/**
	 * @param withdrawAmount the withdrawAmount to set
	 */
	public void setWithdrawAmount(Double withdrawAmount) {
		this.withdrawAmount = withdrawAmount;
	}

	/**
	 * @return the toAddress
	 */
	public String getToAddress() {
		return toAddress;
	}

	/**
	 * @param toAddress the toAddress to set
	 */
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	

}
