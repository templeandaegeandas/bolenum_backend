package com.bolenum.enums;

/**
 * 
 * @author Vishal Kumar
 * @date 09-Oct-2017
 *
 */
public enum OrderStatus {
	SUBMITTED("submitted"), CANCELLED("cancelled"), COMPLETED("completed");

	private String orderStatus;

	private OrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

}
