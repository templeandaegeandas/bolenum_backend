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
	
    private Double feeBTC;
     
    private Double feeOther;

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
     	
}
