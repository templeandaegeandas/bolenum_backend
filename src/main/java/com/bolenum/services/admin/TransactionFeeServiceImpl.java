package com.bolenum.services.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.dto.common.AddTransactioFeeAndLimitForm;
import com.bolenum.model.TransactionFee;
import com.bolenum.repo.admin.TransactionFeeRepo;

/**
 * 
 * @Author Himanshu Kumar
 * @Date 01-Nov-2017
 * 
 */
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
	

	public TransactionFee saveTransactionFee(AddTransactioFeeAndLimitForm addTransactioFeeAndLimitForm) {

		List<TransactionFee> listOfTransactionFee = transactionFeeRepo.findAll();
		if (listOfTransactionFee.size() == 0) {
			transactionFeeRepo.save(new TransactionFee());
			listOfTransactionFee = transactionFeeRepo.findAll();

		}

		TransactionFee transactionFee = listOfTransactionFee.get(0);
		
		if (addTransactioFeeAndLimitForm.getFeeBTC() != null) {
			transactionFee.setFeeBTC(addTransactioFeeAndLimitForm.getFeeBTC());
		}
		if (addTransactioFeeAndLimitForm.getFeeOther() != null) {
			transactionFee.setFeeOther(addTransactioFeeAndLimitForm.getFeeOther());
		}
		if (addTransactioFeeAndLimitForm.getAvailableBalanceLimitToWithdrawForBTC() != null) {
			transactionFee.setAvailableBalanceLimitToWithdrawForBTC(addTransactioFeeAndLimitForm.getAvailableBalanceLimitToWithdrawForBTC());
		}
		if (addTransactioFeeAndLimitForm.getAvailableBalanceLimitToWithdrawForETH() != null) {
			transactionFee.setAvailableBalanceLimitToWithdrawForETH(addTransactioFeeAndLimitForm.getAvailableBalanceLimitToWithdrawForETH());
		}
		if (addTransactioFeeAndLimitForm.getAvailableBalanceLimitToWithdrawForERC20() != null) {
			transactionFee.setAvailableBalanceLimitToWithdrawForERC20(addTransactioFeeAndLimitForm.getAvailableBalanceLimitToWithdrawForERC20());
		}

		if (addTransactioFeeAndLimitForm.getAvailableBalanceLimitToWithdrawForFIAT() != null) {
			transactionFee.setAvailableBalanceLimitToWithdrawForFiat(addTransactioFeeAndLimitForm.getAvailableBalanceLimitToWithdrawForFIAT());
		}

		if (addTransactioFeeAndLimitForm.getMinimumLimitToSendForBTC() != null) {

			transactionFee.setMinimumLimitToSendForBTC(addTransactioFeeAndLimitForm.getMinimumLimitToSendForBTC());
		}

		if (addTransactioFeeAndLimitForm.getMinimumLimitToSendForETH() != null) {
			transactionFee.setMinimumLimitToSendForETH(addTransactioFeeAndLimitForm.getMinimumLimitToSendForETH());
		}

		if (addTransactioFeeAndLimitForm.getMinimumLimitToSendForERC20() != null) {
			transactionFee.setMinimumLimitToSendForERC20(addTransactioFeeAndLimitForm.getMinimumLimitToSendForERC20());
		}

		if (addTransactioFeeAndLimitForm.getMinimumLimitToSendForFIAT() != null) {
			transactionFee.setMinimumLimitToSendForFIAT(addTransactioFeeAndLimitForm.getMinimumLimitToSendForFIAT());
		}

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
	
	@Override
	public TransactionFee getTransactionFeeDetails() {

		List<TransactionFee> listOfTransactionFee = transactionFeeRepo.findAll();
		if (listOfTransactionFee.size() == 0) {
			transactionFeeRepo.save(new TransactionFee());
			listOfTransactionFee = transactionFeeRepo.findAll();

		}
		return listOfTransactionFee.get(0);
		
	}
	
}
