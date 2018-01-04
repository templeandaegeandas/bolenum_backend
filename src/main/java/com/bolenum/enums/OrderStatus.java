package com.bolenum.enums;

/**
 * 
 * @author Vishal Kumar
 * @date 09-Oct-2017
 *
 */
public enum OrderStatus {
	SUBMITTED("submitted"), CANCELLED("cancelled"), COMPLETED("completed"), LOCKED("locked");

	private String status;

	private OrderStatus(String orderStatus) {
		this.status = orderStatus;
	}

	public String getOrderStatus() {
		return status;
	}

}
