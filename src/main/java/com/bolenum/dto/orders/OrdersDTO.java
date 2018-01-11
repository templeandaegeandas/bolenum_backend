/**
 * 
 */
package com.bolenum.dto.orders;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.bolenum.enums.OrderStandard;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;

/**
 * @author chandan kumar singh
 * @date 05-Dec-2017
 */
public class OrdersDTO {
	private Long id;
	@NotNull(message = "Volume must be greater than 0.001")
	private Double volume; // Quantity of buy/sell order
	@NotNull(message = "Volume must be greater than 0.001")
	private Double totalVolume; // total quantity to keep track of initial
								// quantity
	@NotNull(message = "price must be greater than 0")
	private Double price; // price of 1 UNIT

	@Enumerated(EnumType.STRING)
	private OrderStandard orderStandard; // Order is market or limit

	@Enumerated(EnumType.STRING)
	private OrderType orderType; // buy or sell

	private Date createdOn = new Date();

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

	/**
	 * to keep track of which order is matched with incoming order for fiat
	 * order
	 */
	@OneToOne
	private Orders matchedOrder;

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
	 * @return the totalVolume
	 */
	public Double getTotalVolume() {
		return totalVolume;
	}

	/**
	 * @param totalVolume
	 *            the totalVolume to set
	 */
	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
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
	 * @return the orderType
	 */
	public OrderType getOrderType() {
		return orderType;
	}

	/**
	 * @param orderType
	 *            the orderType to set
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
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
	 * @return the deletedOn
	 */
	public Date getDeletedOn() {
		return deletedOn;
	}

	/**
	 * @param deletedOn
	 *            the deletedOn to set
	 */
	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

		/**
	 * @return the orderStatus
	 */
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	/**
	 * @param orderStatus
	 *            the orderStatus to set
	 */
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
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
	 *            the lockedVolume to set
	 */
	public void setLockedVolume(double lockedVolume) {
		this.lockedVolume = lockedVolume;
	}

	/**
	 * @return the isConfirm
	 */
	public boolean isConfirm() {
		return isConfirm;
	}

	/**
	 * @param isConfirm
	 *            the isConfirm to set
	 */
	public void setConfirm(boolean isConfirm) {
		this.isConfirm = isConfirm;
	}

	/**
	 * @return the matchedOrder
	 */
	public Orders getMatchedOrder() {
		return matchedOrder;
	}

	/**
	 * @param matchedOrder
	 *            the matchedOrder to set
	 */
	public void setMatchedOrder(Orders matchedOrder) {
		this.matchedOrder = matchedOrder;
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

	public Orders copy(Orders orders) {
		orders.setId(this.id);
		orders.setVolume(this.volume);
		orders.setTotalVolume(this.totalVolume);
		orders.setPrice(this.price);
		orders.setOrderStandard(this.orderStandard);
		orders.setOrderType(this.orderType);
		orders.setMarketCurrency(marketCurrency);
		orders.setPairedCurrency(pairedCurrency);
		orders.setOrderStatus(this.orderStatus);
		orders.setUser(this.user);
		orders.setLockedVolume(this.lockedVolume);
		orders.setConfirm(this.isConfirm);
		orders.setMatchedOrder(this.matchedOrder);
		return orders;
	}
}
