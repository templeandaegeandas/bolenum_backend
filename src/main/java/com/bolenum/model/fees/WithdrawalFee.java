/**
 * 
 */
package com.bolenum.model.fees;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.bolenum.model.Currency;

/**
 * @author chandan kumar singh
 * @modified Vishal Kumar
 * @date 27-Nov-2017
 */
@Entity
public class WithdrawalFee {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Double fee;
	private Double minWithDrawAmount;
	@OneToOne
	private Currency currency;

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
	 * @return the fee
	 */
	public Double getFee() {
		return fee;
	}

	/**
	 * @param fee
	 *            the fee to set
	 */
	public void setFee(Double fee) {
		this.fee = fee;
	}

	/**
	 * @return the minWithDrawAmount
	 */
	public Double getMinWithDrawAmount() {
		return minWithDrawAmount;
	}

	/**
	 * @param minWithDrawAmount
	 *            the minWithDrawAmount to set
	 */
	public void setMinWithDrawAmount(Double minWithDrawAmount) {
		this.minWithDrawAmount = minWithDrawAmount;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

}
