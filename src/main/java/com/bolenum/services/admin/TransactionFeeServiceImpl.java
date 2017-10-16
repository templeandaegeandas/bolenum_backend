package com.bolenum.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bolenum.model.TransactionFee;
import com.bolenum.repo.admin.TransactionFeeRepo;

@Service
public class TransactionFeeServiceImpl implements TransactionFeeService {

	@Autowired
	private TransactionFeeRepo transactionFeeRepo;

	/**
	 * 
	 * @param amount
	 * @return
	 */
	@Override
	public double getBTCFee(double amount) {
		if (amount > 0) {
			TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
			return ((transactionFee.getFeeBTC() / 100) * amount);
		}
		return 0;
	}

	/**
	 * 
	 */
	@Override
	public double getOtherCryptoFee(double amount) {

		if (amount > 0) {
			TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
			return ((transactionFee.getFeeOther() / 100) * amount);
		}
		return 0;
	}

	/**
	 * 
	 * @return
	 */
	public double getBTCFee() {
		TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
		return transactionFee.getFeeBTC();
	}

	/**
	 * 
	 */
	@Override
	public double getOtherCryptoFee() {
			TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
			return transactionFee.getFeeOther() ;
	}
	
	public TransactionFee saveTransactionFee(TransactionFee transactionFee)
	{
		  return transactionFeeRepo.saveAndFlush(transactionFee);
	}

}
