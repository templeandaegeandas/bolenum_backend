/**
 * 
 */
package com.bolenum.model.fees;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author chandan kumar singh
 * @date 27-Nov-2017
 */
@Entity
public class WithdrawalFee {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Double btc = 0.0;
	private Double eth = 0.0;
	private Double erc20Token = 0.0;

	/**
	 * @return the btc
	 */
	public Double getBtc() {
		return btc;
	}

	/**
	 * @param btc
	 *            the btc to set
	 */
	public void setBtc(Double btc) {
		this.btc = btc;
	}

	/**
	 * @return the eth
	 */
	public Double getEth() {
		return eth;
	}

	/**
	 * @param eth
	 *            the eth to set
	 */
	public void setEth(Double eth) {
		this.eth = eth;
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

}
