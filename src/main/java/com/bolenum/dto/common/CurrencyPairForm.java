package com.bolenum.dto.common;

import java.util.ArrayList;
import java.util.List;
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @Author Himanshu
 * @Date 09-Oct-2017
 */
public class CurrencyPairForm {

	@ApiModelProperty(hidden = true)
	private Long pairId;

	private Currency toCurrency;
	
	private Currency pairedCurrency;

	public Long getPairId() {
		return pairId;
	}

	public void setPairId(Long pairId) {
		this.pairId = pairId;
	}

	public Currency getToCurrency() {
		return toCurrency;
	}

	public void setToCurrency(Currency toCurrency) {
		this.toCurrency = toCurrency;
	}

	public Currency getPairedCurrency() {
		return pairedCurrency;
	}

	public void setPairedCurrency(Currency pairedCurrency) {
		this.pairedCurrency = pairedCurrency;
	}

	public CurrencyPair copy(CurrencyPair currencyPair) {
		List<Currency> toListCurrency=new ArrayList<Currency>();
		toListCurrency.add(this.toCurrency);
		currencyPair.setToCurrency(toListCurrency);
		List<Currency> pairedListCurrency=new ArrayList<Currency>();
		pairedListCurrency.add(this.pairedCurrency);
		currencyPair.setPairedCurrency(pairedListCurrency);
		List<Currency> toCurrency = currencyPair.getToCurrency();
		List<Currency> pairedCurrency = currencyPair.getPairedCurrency();
		String pairName = toCurrency.get(0).getCurrencyAbbreviation() + "/"
				+ pairedCurrency.get(0).getCurrencyAbbreviation();
		currencyPair.setPairName(pairName);
		currencyPair.setIsEnabled(true);
		return currencyPair;
	}

}
