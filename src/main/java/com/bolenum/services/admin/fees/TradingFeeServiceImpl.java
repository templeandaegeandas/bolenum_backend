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
			if (tradingFee.getBTC() != null) {
				trading.setBTC(tradingFee.getBTC());
			}
			if (tradingFee.getETH() != null) {
				trading.setETH(tradingFee.getETH());
			}
			if (tradingFee.getErc20Token() != null) {
				trading.setErc20Token(tradingFee.getErc20Token());
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
