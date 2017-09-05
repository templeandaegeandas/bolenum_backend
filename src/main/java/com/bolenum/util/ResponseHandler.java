/**
 * 
 */
package com.bolenum.util;

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
	};

	public static ResponseEntity<Map<String, Object>> response(Map<String, Object> result, HttpStatus httpStatus, String message,boolean isError,Object responseObject) {
		result.put("data", responseObject);
		result.put("message", message);
		result.put("isError", isError);
		result.put("status", httpStatus.value());
		return new ResponseEntity<Map<String, Object>>(result, httpStatus);
	}
}
