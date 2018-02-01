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
	
	
	/**
	 * This method is use to get all withdrawal fee
	 * @param nothing
	 * @return WithdrawalFee
	 */
	public List<WithdrawalFee> getAllWithdrawalFee();

	/**
	 * This method is use to save withdrawal fee
	 * @param withdrawal fee
	 * @return WithdrawalFee
	 */
	public WithdrawalFee saveWithdrawalFee(WithdrawalFee withdrawalFee);

	/**
	 * This method is use to get withdrawal fee
	 * @param currenecy id
	 * @return WithdrawalFee
	 */
	public WithdrawalFee getWithdrawalFee(long currencyId);
}
