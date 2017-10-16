package com.bolenum.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TransactionFee {
	
	/**
	 *  transaction fee for BTC in case of deposit and withdrawal  
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
    private double feeBTC;
     
    private double feeOther;

	public double getFeeBTC() {
		return feeBTC;
	}

	public void setFeeBTC(double feeBTC) {
		this.feeBTC = feeBTC;
	}

	public double getFeeOther() {
		return feeOther;
	}

	public void setFeeOther(double feeOther) {
		this.feeOther = feeOther;
	}
     	
}
