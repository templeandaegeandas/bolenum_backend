package com.bolenum.model.fees;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @Author himanshu
 * @Date 01-Nov-2017
 */
@Entity
public class TradingFee {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Double btc;
	private Double eth;
	private Double erc20Token;
	private Double fiat;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the bTC
	 */
	public Double getBTC() {
		return btc;
	}

	/**
	 * @param bTC
	 *            the bTC to set
	 */
	public void setBTC(Double bTC) {
		btc = bTC;
	}

	/**
	 * @return the eTH
	 */
	public Double getETH() {
		return eth;
	}

	/**
	 * @param eTH
	 *            the eTH to set
	 */
	public void setETH(Double eTH) {
		eth = eTH;
	}

	/**
	 * @return the erc20Token
	 */
	public Double getErc20Token() {
		return erc20Token;
	}

	/**
	 * @param erc20Token
	 *            the erc20Token to set
	 */
	public void setErc20Token(Double erc20Token) {
		this.erc20Token = erc20Token;
	}

	/**
	 * @return the fiat
	 */
	public Double getFiat() {
		return fiat;
	}

	/**
	 * @param fiat
	 *            the fiat to set
	 */
	public void setFiat(Double fiat) {
		this.fiat = fiat;
	}

}
