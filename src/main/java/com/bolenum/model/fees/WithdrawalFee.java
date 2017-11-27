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
	private Double btc;
	private Double eth;
	private Double erc20Token ;
	private Double minWithDrawAmountBtc;
	private Double minWithDrawAmountEth;
	private Double minWithDrawAmountErc20Token;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

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

	/**
	 * @return the minWithDrawAmountBtc
	 */
	public Double getMinWithDrawAmountBtc() {
		return minWithDrawAmountBtc;
	}

	/**
	 * @param minWithDrawAmountBtc
	 *            the minWithDrawAmountBtc to set
	 */
	public void setMinWithDrawAmountBtc(Double minWithDrawAmountBtc) {
		this.minWithDrawAmountBtc = minWithDrawAmountBtc;
	}

	/**
	 * @return the minWithDrawAmountEth
	 */
	public Double getMinWithDrawAmountEth() {
		return minWithDrawAmountEth;
	}

	/**
	 * @param minWithDrawAmountEth
	 *            the minWithDrawAmountEth to set
	 */
	public void setMinWithDrawAmountEth(Double minWithDrawAmountEth) {
		this.minWithDrawAmountEth = minWithDrawAmountEth;
	}

	/**
	 * @return the minWithDrawAmountErc20Token
	 */
	public Double getMinWithDrawAmountErc20Token() {
		return minWithDrawAmountErc20Token;
	}

	/**
	 * @param minWithDrawAmountErc20Token
	 *            the minWithDrawAmountErc20Token to set
	 */
	public void setMinWithDrawAmountErc20Token(Double minWithDrawAmountErc20Token) {
		this.minWithDrawAmountErc20Token = minWithDrawAmountErc20Token;
	}

}
