package com.bolenum.dto.common;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.bolenum.enums.CurrencyType;
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

	@NotNull
	private Currency toCurrency;

	@NotNull
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
		String pairName;
		List<Currency> toListCurrency = new ArrayList<>();
		toListCurrency.add(this.toCurrency);
		currencyPair.setToCurrency(toListCurrency);
		List<Currency> pairedListCurrency = new ArrayList<>();
		pairedListCurrency.add(this.pairedCurrency);
		currencyPair.setPairedCurrency(pairedListCurrency);
		if (CurrencyType.FIAT.equals(this.toCurrency.getCurrencyType())
				|| CurrencyType.FIAT.equals(this.pairedCurrency.getCurrencyType())) {
			pairName = currencyPair.getToCurrency().get(0).getCurrencyAbbreviation() + "/"
					+ currencyPair.getPairedCurrency().get(0).getCurrencyAbbreviation();
		} else {
			pairName = currencyPair.getPairedCurrency().get(0).getCurrencyAbbreviation() + "/"
					+ currencyPair.getToCurrency().get(0).getCurrencyAbbreviation();
		}
		currencyPair.setPairName(pairName);
		currencyPair.setIsEnabled(true);
		return currencyPair;
	}

}
