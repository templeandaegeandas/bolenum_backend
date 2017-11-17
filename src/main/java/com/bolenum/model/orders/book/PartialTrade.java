/**
 * 
 */
package com.bolenum.model.orders.book;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.bolenum.enums.OrderStandard;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 15-Nov-2017
 */
@Entity
public class PartialTrade {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Double price;
	private Double volume;
	@OneToOne
	private User buyer;
	@OneToOne
	private User seller;
	@OneToOne
	private CurrencyPair pair;
	@Enumerated(EnumType.STRING)
	private OrderStandard orderStandard;
	@CreationTimestamp
	private Date createdOn;
	private Date updatedOn = new Date();

	public PartialTrade() {

	}

	public PartialTrade(Double price, Double volume, User buyer, User seller, CurrencyPair pair,
			OrderStandard orderStandard) {
		this.price = price;
		this.volume = volume;
		this.buyer = buyer;
		this.seller = seller;
		this.pair = pair;
		this.orderStandard = orderStandard;
	}

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
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/**
	 * @return the volume
	 */
	public Double getVolume() {
		return volume;
	}

	/**
	 * @param volume
	 *            the volume to set
	 */
	public void setVolume(Double volume) {
		this.volume = volume;
	}

	/**
	 * @return the buyer
	 */
	public User getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer
	 *            the buyer to set
	 */
	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the seller
	 */
	public User getSeller() {
		return seller;
	}

	/**
	 * @param seller
	 *            the seller to set
	 */
	public void setSeller(User seller) {
		this.seller = seller;
	}

	/**
	 * @return the pair
	 */
	public CurrencyPair getPair() {
		return pair;
	}

	/**
	 * @param pair
	 *            the pair to set
	 */
	public void setPair(CurrencyPair pair) {
		this.pair = pair;
	}

	/**
	 * @return the orderStandard
	 */
	public OrderStandard getOrderStandard() {
		return orderStandard;
	}

	/**
	 * @param orderStandard
	 *            the orderStandard to set
	 */
	public void setOrderStandard(OrderStandard orderStandard) {
		this.orderStandard = orderStandard;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn
	 *            the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the updatedOn
	 */
	public Date getUpdatedOn() {
		return updatedOn;
	}

	/**
	 * @param updatedOn
	 *            the updatedOn to set
	 */
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

}
