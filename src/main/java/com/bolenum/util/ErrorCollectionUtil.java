/**
 * 
 */
package com.bolenum.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public class ErrorCollectionUtil {
	private ErrorCollectionUtil() {
	}

	/**
	 * This method is use to get Error Map
	 * @param bindingResult
	 * @return
	 */
	public static Map<String, Object> getErrorMap(BindingResult bindingResult) {
		Map<String, Object> errors = new LinkedHashMap<>();
		bindingResult.getFieldErrors()
				.forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
		return errors;
	}

	/**
	 * This method is use to get Error
	 * @param bindingResult
	 * @return
	 */
	public static String getError(BindingResult bindingResult) {
		List<FieldError> list = bindingResult.getFieldErrors();
		return list.get(0).getDefaultMessage();
	}
}
