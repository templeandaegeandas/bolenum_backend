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
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.User;

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
	@OneToOne
	private User buyer;
	@OneToOne
	private User seller;
	@OneToOne
	private CurrencyPair pair;
	@Enumerated(EnumType.STRING)
	private OrderStandard orderStandard;
	private Date createdOn = new Date();
	private Double buyerTradeFee;
	private Double sellerTradeFee;
	private Boolean isFeeDeducted;
	
	public Trade(Double price, Double volume, User buyer, User seller, CurrencyPair pair, OrderStandard orderStandard,
			Double buyerTradeFee, Double sellerTradeFee) {
		this.price = price;
		this.volume = volume;
		this.buyer = buyer;
		this.seller = seller;
		this.pair = pair;
		this.orderStandard = orderStandard;
		this.buyerTradeFee = buyerTradeFee;
		this.sellerTradeFee = sellerTradeFee;
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

	public User getBuyer() {
		return buyer;
	}

	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}

	public User getSeller() {
		return seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	public CurrencyPair getPair() {
		return pair;
	}

	public void setPair(CurrencyPair pair) {
		this.pair = pair;
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

	/**
	 * @return the buyerTradeFee
	 */
	public Double getBuyerTradeFee() {
		return buyerTradeFee;
	}

	/**
	 * @param buyerTradeFee the buyerTradeFee to set
	 */
	public void setBuyerTradeFee(Double buyerTradeFee) {
		this.buyerTradeFee = buyerTradeFee;
	}

	/**
	 * @return the sellerTradeFee
	 */
	public Double getSellerTradeFee() {
		return sellerTradeFee;
	}

	/**
	 * @param sellerTradeFee the sellerTradeFee to set
	 */
	public void setSellerTradeFee(Double sellerTradeFee) {
		this.sellerTradeFee = sellerTradeFee;
	}

	/**
	 * @return the isFeeDeducted
	 */
	public Boolean getIsFeeDeducted() {
		return isFeeDeducted;
	}

	/**
	 * @param isFeeDeducted the isFeeDeducted to set
	 */
	public void setIsFeeDeducted(Boolean isFeeDeducted) {
		this.isFeeDeducted = isFeeDeducted;
	}
	
	
}
