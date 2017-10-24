package com.bolenum.services.admin;

import java.util.List;
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
	 * to get transaction fee for BTC
	 * 
	 * @return
	 */
	public Double getBTCFee() {
		TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
		return transactionFee.getFeeBTC();
	}

	/**
	 * to get transaction fee for Other cryptocurrency
	 */
	@Override
	public Double getOtherCryptoFee() {
		TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
		return transactionFee.getFeeOther();
	}

	public TransactionFee saveTransactionFee(TransactionFee transactionFee) {
		return transactionFeeRepo.saveAndFlush(transactionFee);
	}

	/**
	 * to get list of transaction fee already entered by admin with help of this
	 * updation can be done in transaction fee
	 */
	@Override
	public List<TransactionFee> getListOfTransactionFee() {

		return transactionFeeRepo.findAll();
	}

	/**
	 * to update transaction fee in case of transaction fee already available
	 */
	@Override
	public TransactionFee updateTransactionFee(TransactionFee existingTransactionFee, TransactionFee transactionFee) {
		if (transactionFee.getFeeBTC() != null) {
			existingTransactionFee.setFeeBTC(transactionFee.getFeeBTC());
		}

		if (transactionFee.getFeeOther() != null) {
			existingTransactionFee.setFeeBTC(transactionFee.getFeeBTC());
		}
		return transactionFeeRepo.saveAndFlush(existingTransactionFee);
	}

//	@Override
//	public Double getBTCFee(Double amount) {
//		if (amount != null) {
//			TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
//			if (transactionFee.getFeeBTC() != null) {
//				return (transactionFee.getFeeBTC() * amount);
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public Double getOtherCryptoFee(Double amount) {
//		if (amount != null) {
//			TransactionFee transactionFee = transactionFeeRepo.getOne(1L);
//			if (transactionFee.getFeeOther() != null) {
//				return (transactionFee.getFeeOther() * amount);
//			}
//		}
//		return null;
//	}
}
