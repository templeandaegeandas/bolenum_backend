package com.bolenum.model.orders.book;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.bolenum.enums.OrderStandard;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
	private Double volume; // Quantity of buy/sell order
	private Double totalVolume; // total quantity to keep track of initial
								// quantity
	private Double price; // price of 1 UNIT

	@Enumerated(EnumType.STRING)
	private OrderStandard orderStandard; // Order is market or limit

	@Enumerated(EnumType.STRING)
	private OrderType orderType; // buy or sell

	private Date createdOn = new Date();
	
	private Date matchedOn;

	private Date deletedOn;
	private boolean isDeleted;

	@OneToOne
	private Currency marketCurrency;
	
	@OneToOne
	private Currency pairedCurrency;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus = OrderStatus.SUBMITTED;
	@OneToOne
	private User user;

	private double lockedVolume;

	private boolean isConfirm;
	
	private boolean isDispute;
	
	/**
	 * to keep track of which order is matched with incoming order for fiat order
	 */
	@OneToOne
	@JsonIgnore
	private Orders matchedOrder;

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

	public Date getMatchedOn() {
		return matchedOn;
	}

	public void setMatchedOn(Date matchedOn) {
		this.matchedOn = matchedOn;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the marketCurrency
	 */
	public Currency getMarketCurrency() {
		return marketCurrency;
	}

	/**
	 * @param marketCurrency the marketCurrency to set
	 */
	public void setMarketCurrency(Currency marketCurrency) {
		this.marketCurrency = marketCurrency;
	}

	/**
	 * @return the pairedCurrency
	 */
	public Currency getPairedCurrency() {
		return pairedCurrency;
	}

	/**
	 * @param pairedCurrency the pairedCurrency to set
	 */
	public void setPairedCurrency(Currency pairedCurrency) {
		this.pairedCurrency = pairedCurrency;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the lockedVolume
	 */
	public double getLockedVolume() {
		return lockedVolume;
	}

	/**
	 * @param lockedVolume
	 */
	public void setLockedVolume(double lockedVolume) {
		this.lockedVolume = lockedVolume;
	}

	public Orders getMatchedOrder() {
		return matchedOrder;
	}

	public void setMatchedOrder(Orders matchedOrder) {
		this.matchedOrder = matchedOrder;
	}

	/**
	 * this will return true if buyer has confirmed that fiat currency has been
	 * deposited to seller account, then seller will get notification.
	 */
	public boolean isConfirm() {
		return isConfirm;
	}

	/**
	 * set true when buyer paid FIAT to seller bank account, default false
	 * 
	 * @param true
	 */
	public void setConfirm(boolean isConfirm) {
		this.isConfirm = isConfirm;
	}

	public boolean isDispute() {
		return isDispute;
	}

	public void setDispute(boolean isDispute) {
		this.isDispute = isDispute;
	}
}
