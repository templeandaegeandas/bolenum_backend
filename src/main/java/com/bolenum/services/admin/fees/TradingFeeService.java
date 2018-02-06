package com.bolenum.services.admin.fees;

import java.util.List;

import com.bolenum.model.fees.TradingFee;

/**
 * 
 * @Author Himanshu Kumar
 * @Date 01-Nov-2017
 * 
 */
public interface TradingFeeService {

	/**
	 * This method is use to get all trading fee.
	 * @param nothing
	 * @return Trading Fee
	 */
	public List<TradingFee> getAllTradingFee();

	/**
	 * This method is use to save all trading fee.
	 * @param trading fee
	 * @return Trading Fee
	 */
	public TradingFee saveTradingFee(TradingFee tradingFee);

	/**
	 * This method is use to get trading fee.
	 * @param nothing
	 * @return Trading Fee
	 */
	public TradingFee getTradingFee();
	
	/**
	 * This method is use to calculate trading fee.
	 * @param amount
	 * @return double
	 */
	public double calculateFee(double amount);
}
