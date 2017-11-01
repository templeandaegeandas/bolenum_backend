package com.bolenum.services.admin;

import com.bolenum.dto.common.AddTransactioFeeAndLimitForm;
import com.bolenum.model.TransactionFee;

/**
 * 
 * @Author Himanshu Kumar
 * @Date 01-Nov-2017
 * 
 */
public interface TransactionFeeService {

	public Double getOtherCryptoFee(Double amount);

	public Double getBTCFee(Double amount);

	public Double getOtherCryptoFee();
	
	public Double getBTCFee();
	
	public TransactionFee saveTransactionFee(AddTransactioFeeAndLimitForm addTransactioFeeAndLimitForm);

	public TransactionFee getTransactionFeeDetails();
}
