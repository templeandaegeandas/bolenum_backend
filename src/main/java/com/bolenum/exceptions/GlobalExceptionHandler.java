package com.bolenum.exceptions;

import java.io.IOException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bolenum.services.common.LocaleService;
import com.bolenum.util.ResponseHandler;

/**
 * 
 * @author Vishal Kumar
 * @date 14-Sep-2017
 *
 */

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private LocaleService localeService;

	public GlobalExceptionHandler() {
		super();
	}

	/**
	 * to handle Illegal Argument Exception
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	@ExceptionHandler({ IllegalArgumentException.class })
	public ResponseEntity<Object> handleIllegalArgumentException(final IllegalArgumentException ex, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}
	
	/**
<<<<<<< HEAD
	 * to handle IOException
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */
	@ExceptionHandler({ IOException.class })
	public ResponseEntity<Object> handleIOException(final IOException ex, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}
	
	/**
=======
>>>>>>> 00b389e80ca195c913eeaad89ee7c31737c44140
	 * 
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleBadRequest(final ConstraintViolationException ex, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("constraint.violent"), null);
	}

	/**
	 * 
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	@ExceptionHandler({ DataIntegrityViolationException.class })
	public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("constraint.violent"), null);
	}

	/**
	 * 
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */
	@ExceptionHandler({ InvalidPasswordException.class })
	public ResponseEntity<Object> handleBadRequest(final InvalidPasswordException ex, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/**
	 * 
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */
	@ExceptionHandler({ PersistenceException.class })
	public ResponseEntity<Object> handleBadRequest(final PersistenceException ex, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/**
	 * 
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */
	@ExceptionHandler({ MaxSizeExceedException.class })
	public ResponseEntity<Object> handleBadRequest(final MaxSizeExceedException ex, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}
	
	/**
	 * 
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */
	@ExceptionHandler({ MobileNotVerifiedException.class })
	public ResponseEntity<Object> handleBadRequest(final MobileNotVerifiedException ex, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status,
			final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
		// return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(),
		// );
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(final HttpMessageNotWritableException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		ex.printStackTrace();
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
		// return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(),
		// );
	}
	// 403

	/**
	 * 
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<Object> handleAccessDeniedException(final Exception ex, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.FORBIDDEN, true, ex.getMessage(), null);
	}

	// 404

	/**
	 * 
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	@ExceptionHandler(value = { EntityNotFoundException.class })
	protected ResponseEntity<Object> handleNotFound(final RuntimeException ex, final WebRequest request) {
		return ResponseHandler.response(HttpStatus.NOT_FOUND, true, ex.getMessage(), null);
	}

	// 409

	/**
	 * 
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	@ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class })
	protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
		ex.printStackTrace();
		return ResponseHandler.response(HttpStatus.CONFLICT, true, ex.getMessage(), null);
	}

	// 412

	// 500

	/**
	 * 
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	@ExceptionHandler({ NullPointerException.class, IllegalStateException.class})
	public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
		logger.error("500 Status Code");
		ex.printStackTrace();
		return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true, ex.getMessage(), null);
	}

}