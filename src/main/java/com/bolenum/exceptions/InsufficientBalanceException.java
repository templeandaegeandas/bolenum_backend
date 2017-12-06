package com.bolenum.exceptions;

public class InsufficientBalanceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1290250803258271216L;

	public InsufficientBalanceException(String msg) {
		super(msg);
	}

}
