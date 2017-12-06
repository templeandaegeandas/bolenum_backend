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

	private Double fee;
	private Double fiat;

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
	public Double getFee() {
		return fee;
	}

	/**
	 * @param btc
	 *            the btc to set
	 */
	public void setFee(Double fee) {
		this.fee = fee;
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
