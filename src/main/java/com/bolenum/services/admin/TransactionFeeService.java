package com.bolenum.services.admin;

import java.util.List;

import com.bolenum.model.TransactionFee;

public interface TransactionFeeService {

	public Double getOtherCryptoFee(Double amount);

	public Double getBTCFee(Double amount);

	public Double getOtherCryptoFee();

	public Double getBTCFee();

	public TransactionFee saveTransactionFee(TransactionFee transactionFee);

	public List<TransactionFee> getListOfTransactionFee();

	public TransactionFee updateTransactionFee(TransactionFee existingTransactionFee, TransactionFee transactionFee);

}
