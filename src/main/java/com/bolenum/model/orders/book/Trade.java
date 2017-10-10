package com.bolenum.model.orders.book;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.bolenum.enums.OrderStandard;

/**
 * 
 * @author Vishal Kumar
 * @date 09-Oct-2017
 *
 */
@Entity
public class Trade {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Double price;
	private Double volume;
	private Long buyerId;
	private Long sellerId;
	private OrderStandard orderStandard;
	private Date createdOn = new Date();

	public Trade(Double price, Double volume, Long buyerId, Long sellerId,
			OrderStandard orderStandard) {
		this.price = price;
		this.volume = volume;
		this.buyerId = buyerId;
		this.sellerId = sellerId;
		this.orderStandard = orderStandard;
	}

	public Trade() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public OrderStandard getOrderStandard() {
		return orderStandard;
	}

	public void setOrderStandard(OrderStandard orderStandard) {
		this.orderStandard = orderStandard;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}
