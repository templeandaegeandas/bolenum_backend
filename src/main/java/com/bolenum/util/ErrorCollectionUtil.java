/**
 * 
 */
package com.bolenum.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

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
		bindingResult.getFieldErrors().forEach(new Consumer<FieldError>() {
			@Override
			public void accept(FieldError fieldError) {
				errors.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
		});
		return errors;
	}
}
