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
	public Double getBTCFee(Double amount) {
		if (amount != null) {
			TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
			if (transactionFee.getFeeBTC() != null) {
				return ((transactionFee.getFeeBTC() / 100) * amount);
			}
		}
		return null;
	}

	/**
	 * 
	 */
	@Override
	public Double getOtherCryptoFee(Double amount) {
		if (amount != null) {
			TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
			if (transactionFee.getFeeOther() != null) {
				return ((transactionFee.getFeeOther() / 100) * amount);
			}
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public Double getBTCFee() {
		TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
		return transactionFee.getFeeBTC();
	}

	/**
	 * 
	 */
	@Override
	public Double getOtherCryptoFee() {
		TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
		return transactionFee.getFeeOther();
	}

	public TransactionFee saveTransactionFee(TransactionFee transactionFee) {
		return transactionFeeRepo.saveAndFlush(transactionFee);
	}

	/*
	 * @Override public Double getBTCFee(Double amount) { if (amount > 0) {
	 * TransactionFee transactionFee = transactionFeeRepo.getOne(1L); return
	 * (transactionFee.getFeeBTC() * amount); } return null; }
	 */

	/*
	 * @Override public Double getOtherCryptoFee() { if (amount > 0) {
	 * TransactionFee transactionFee = transactionFeeRepo.getOne(1L); return
	 * (transactionFee.getFeeOther() * amount); } return null; }
	 */
}
