/**
 * 
 */
package com.bolenum.services.admin.fees;

import java.util.List;

import com.bolenum.model.fees.WithdrawalFee;

/**
 * @author chandan kumar singh
 * @date 27-Nov-2017
 */
public interface WithdrawalFeeService {
	public List<WithdrawalFee> getAllWithdrawalFee();

	public WithdrawalFee saveWithdrawalFee(WithdrawalFee withdrawalFee);

	public WithdrawalFee getWithdrawalFee(long currencyId);
}
