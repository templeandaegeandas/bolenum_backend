package com.bolenum.model.orders.book;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.bolenum.enums.OrderStandard;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;

/**
 * 
 * @author Vishal Kumar
 * @date 06-Oct-2017
 *
 */
@Entity
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Double volume;
	private Double totalVolume;
	private Double price;
	private OrderStandard orderStandard;
	private OrderType orderType;
	private Date createdOn = new Date();
	private Date deletedOn;
	private Long pairId;
	private OrderStatus orderStatus = OrderStatus.SUBMITTED;
	private Long userId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public OrderStandard getOrderStandard() {
		return orderStandard;
	}

	public void setOrderStandard(OrderStandard orderStandard) {
		this.orderStandard = orderStandard;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	public Long getPairId() {
		return pairId;
	}

	public void setPairId(Long pairId) {
		this.pairId = pairId;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
