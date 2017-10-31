package com.bolenum.services.admin;

import com.bolenum.model.TransactionFee;

public interface TransactionFeeService {

	public Double getOtherCryptoFee(Double amount);

	public Double getBTCFee(Double amount);

	public Double getOtherCryptoFee();
	
	public Double getBTCFee();
	
	public TransactionFee saveTransactionFee(TransactionFee transactionFee);

}
