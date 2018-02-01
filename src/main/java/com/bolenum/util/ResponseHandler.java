/**
 * 
 */
package com.bolenum.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @Author chandan Kumar Singh
 *
 * @Date 05-Sep-2017
 */

public class ResponseHandler {

	private ResponseHandler() {
	}

	/**
	 * This method is use for response
	 * @param httpStatus
	 * @param isError
	 * @param message
	 * @param responseObject
	 * @return
	 */
	public static ResponseEntity<Object> response(HttpStatus httpStatus, Boolean isError, String message,
			Object responseObject) {
		Map<String, Object> map = new HashMap<>();
		map.put("timestamp", new Date().getTime());
		map.put("status", httpStatus.value());
		map.put("isError", isError);
		map.put("message", message);
		map.put("data", responseObject);
		return new ResponseEntity<>(map, httpStatus);
	}
}
