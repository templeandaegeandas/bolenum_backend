/**
 * 
 */
package com.bolenum.services.admin.fees;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.fees.WithdrawalFee;
import com.bolenum.repo.admin.WithdrawalFeeRepo;

/**
 * @author chandan kumar singh
 * @date 27-Nov-2017
 */
@Service
public class WithdrawalFeeServiceImpl implements WithdrawalFeeService {

	@Autowired
	private WithdrawalFeeRepo withdrawalFeeRepo;

	@Override
	public List<WithdrawalFee> getAllWithdrawalFee() {
		return withdrawalFeeRepo.findAll();
	}

	@Override
	public WithdrawalFee saveWithdrawalFee(WithdrawalFee withdrawalFee) {
		WithdrawalFee withdrawal = withdrawalFeeRepo.findByCurrencyCurrencyId(withdrawalFee.getCurrency().getCurrencyId());
		if (withdrawal != null) {
			if (withdrawalFee.getFee() != null) {
				withdrawal.setFee(withdrawalFee.getFee());
			}
			if (withdrawalFee.getMinWithDrawAmount() != null) {
				withdrawal.setMinWithDrawAmount(withdrawalFee.getMinWithDrawAmount());
			}
			withdrawal = withdrawalFeeRepo.saveAndFlush(withdrawal);
		} else {
			withdrawal = withdrawalFeeRepo.saveAndFlush(withdrawalFee);
		}

		return withdrawal;
	}

	@Override
	public WithdrawalFee getWithdrawalFee(long currencyId) {
		return withdrawalFeeRepo.findByCurrencyCurrencyId(currencyId);
	}

}
