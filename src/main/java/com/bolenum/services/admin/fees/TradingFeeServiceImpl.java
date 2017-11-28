package com.bolenum.services.admin.fees;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.fees.TradingFee;
import com.bolenum.repo.admin.TradingFeeRepo;

/**
 * 
 * @Author Himanshu Kumar
 * @Date 01-Nov-2017
 * @modified Chandan
 */
@Service
public class TradingFeeServiceImpl implements TradingFeeService {

	@Autowired
	private TradingFeeRepo tradingFeeRepo;

	@Override
	public List<TradingFee> getAllTradingFee() {
		return tradingFeeRepo.findAll();
	}

	@Override
	public TradingFee saveTradingFee(TradingFee tradingFee) {
		TradingFee trading = tradingFeeRepo.findOne(1L);
		if (trading != null) {
			if (tradingFee.getFee() != null) {
				trading.setFee(tradingFee.getFee());
			}
			if (tradingFee.getFiat() != null) {
				trading.setFiat(tradingFee.getFiat());
			}
			trading = tradingFeeRepo.saveAndFlush(trading);
		} else {
			trading = tradingFeeRepo.saveAndFlush(tradingFee);
		}

		return trading;
	}

	@Override
	public TradingFee getTradingFee() {
		return tradingFeeRepo.findOne(1L);
	}

}
