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
	 *
	 * @return userId
	 */
	public Long getUserId() {
		return userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getBalanceBTC() {
		return balanceBTC;
	}

	public void setBalanceBTC(String balanceBTC) {
		this.balanceBTC = balanceBTC;
	}

	public Double getBalanceETH() {
		return balanceETH;
	}

	public void setBalanceETH(Double balanceETH) {
		this.balanceETH = balanceETH;
	}

	public Double getBalanceBLN() {
		return balanceBLN;
	}

	public void setBalanceBLN(Double balanceBLN) {
		this.balanceBLN = balanceBLN;
	}

}
