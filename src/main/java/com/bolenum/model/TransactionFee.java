package com.bolenum.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 * 
 * @Author himanshu
 * @Date 01-Nov-2017
 */
@Entity
public class TransactionFee {
	
	/**
	 *  transaction fee for BTC in case of deposit and withdrawal  
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
    private Double feeBTC;
     
    private Double feeOther;
    
    private Double availableBalanceLimitToWithdrawForBTC;
    
    private Double availableBalanceLimitToWithdrawForETH;
    
    private Double availableBalanceLimitToWithdrawForERC20;
    
    private Double availableBalanceLimitToWithdrawForFiat;
    
    private Double minimumLimitToSendForBTC;
    
    private Double minimumLimitToSendForETH;
    
    private Double minimumLimitToSendForERC20;
    
    private Double minimumLimitToSendForFIAT;
    
    public TransactionFee()
    {    	
    	feeBTC=0.00;
    	
    	feeOther=0.00;
    	
    	availableBalanceLimitToWithdrawForBTC=0.00;
    	
    	availableBalanceLimitToWithdrawForETH=0.00;
    	
    	availableBalanceLimitToWithdrawForERC20=0.00;
    	
    	availableBalanceLimitToWithdrawForFiat=0.00;
    	
    	minimumLimitToSendForBTC=0.00;
        
        minimumLimitToSendForETH=0.00;
        
        minimumLimitToSendForERC20=0.00;
        
        minimumLimitToSendForFIAT=0.00;
        
    }

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

	public Double getAvailableBalanceLimitToWithdrawForFiat() {
		return availableBalanceLimitToWithdrawForFiat;
	}

	public void setAvailableBalanceLimitToWithdrawForFiat(Double availableBalanceLimitToWithdrawForFiat) {
		this.availableBalanceLimitToWithdrawForFiat = availableBalanceLimitToWithdrawForFiat;
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
