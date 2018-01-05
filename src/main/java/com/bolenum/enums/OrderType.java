package com.bolenum.enums;

/**
 * 
 * @author Vishal Kumar
 * @date 09-Oct-2017
 *
 */
public enum OrderType {
	BUY("buy"), SELL("sell");

	private String type;

	private OrderType(String orderType) {
		this.type = orderType;
	}

	public String getOrderType() {
		return type;
	}
}
