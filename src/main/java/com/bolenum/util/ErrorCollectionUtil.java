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

	public static Map<String, Object> getErrorMap(BindingResult bindingResult) {
		Map<String, Object> errors = new LinkedHashMap<String, Object>();
		// bindingResult.getFieldErrors().forEach(new Consumer<FieldError>() {
		// @Override
		// public void accept(FieldError fieldError) {
		// errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		// }
		// });
		bindingResult.getFieldErrors().forEach(fieldError -> {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		});
		return errors;
	}

	public static String getError(BindingResult bindingResult) {
		List<FieldError> list = bindingResult.getFieldErrors();
		String message = list.get(0).getDefaultMessage();
		return message;
	}
}
