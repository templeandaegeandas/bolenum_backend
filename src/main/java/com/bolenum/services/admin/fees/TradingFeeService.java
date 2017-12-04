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

	public List<TradingFee> getAllTradingFee();

	public TradingFee saveTradingFee(TradingFee tradingFee);

	public TradingFee getTradingFee();
	
	public double calculateFee(double amount);
}
