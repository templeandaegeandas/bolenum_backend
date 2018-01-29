package com.bolenum.enums;

/**
 * 
 * @author Vishal Kumar
 * @date 09-Oct-2017
 *
 */
public enum OrderStandard {
	MARKET("market"), LIMIT("limit");

	private String orderStd;

	private OrderStandard(String orderStd) {
		this.orderStd = orderStd;
	}

	/**
	 * This method is use to get order standard.
	 * @param Nothing
	 * @return orderStd
	 */
	public String getOrderStandard() {
		return orderStd;
	}

}
