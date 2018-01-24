package com.bolenum.model.coin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WalletBalance {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	Long userId;

	String balanceBTC;

	Double balanceETH;

	Double balanceBLN;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the balanceBTC
	 */
	public String getBalanceBTC() {
		return balanceBTC;
	}

	/**
	 * @param balanceBTC the balanceBTC to set
	 */
	public void setBalanceBTC(String balanceBTC) {
		this.balanceBTC = balanceBTC;
	}

	/**
	 * @return the balanceETH
	 */
	public Double getBalanceETH() {
		return balanceETH;
	}

	/**
	 * @param balanceETH the balanceETH to set
	 */
	public void setBalanceETH(Double balanceETH) {
		this.balanceETH = balanceETH;
	}

	/**
	 * @return the balanceBLN
	 */
	public Double getBalanceBLN() {
		return balanceBLN;
	}

	/**
	 * @param balanceBLN the balanceBLN to set
	 */
	public void setBalanceBLN(Double balanceBLN) {
		this.balanceBLN = balanceBLN;
	}
}
