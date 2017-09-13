/**
 * 
 */
package com.bolenum.exceptions;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public class InvalidPasswordException extends Exception {

	private static final long serialVersionUID = 1L;
	public InvalidPasswordException(String msg){
		super(msg);
	}
}
