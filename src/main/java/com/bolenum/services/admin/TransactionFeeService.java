package com.bolenum.services.admin;

import com.bolenum.model.TransactionFee;

public interface TransactionFeeService {

	public double getOtherCryptoFee(double amount);

	public double getBTCFee(double amount);

	public double getOtherCryptoFee();
	
	public double getBTCFee();
	
	public TransactionFee saveTransactionFee(TransactionFee transactionFee);

}
