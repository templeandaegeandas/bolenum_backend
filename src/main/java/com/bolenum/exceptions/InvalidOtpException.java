package com.bolenum.exceptions;

public class InvalidOtpException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidOtpException(String msg){
		super(msg);
	}

}
