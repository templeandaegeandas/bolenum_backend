package com.bolenum.exceptions;

import java.io.IOException;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
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
	 * This method is use to handle Illegal Argument Exception
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	// @ExceptionHandler({ IllegalArgumentException.class })
	public ResponseEntity<Object> handleIllegalArgumentException(final IllegalArgumentException ex,
			final WebRequest request) {
		logger.error("IllegalArgumentException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/**
	 * This method is use to handle IOException
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */
	// @ExceptionHandler({ IOException.class })
	public ResponseEntity<Object> handleIOException(final IOException ex, final WebRequest request) {
		logger.error("IOException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/**
	 * This method is use to handle bad request.
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	// @ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleBadRequest(final ConstraintViolationException ex, final WebRequest request) {
		logger.error("ConstraintViolationException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("constraint.violent"),
				null);
	}

	/**
	 * This method is use to handle bad request.
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	// @ExceptionHandler({ DataIntegrityViolationException.class })
	public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
		logger.error("DataIntegrityViolationException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, localeService.getMessage("constraint.violent"),
				null);
	}

	/**
	 * This method is use to handle bad request.
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */
	@ExceptionHandler({ InvalidPasswordException.class })
	public ResponseEntity<Object> handleBadRequest(final InvalidPasswordException ex, final WebRequest request) {
		logger.error("InvalidPasswordException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/**
	 * This method is use to handle bad request.
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */
	// @ExceptionHandler({ PersistenceException.class })
	public ResponseEntity<Object> handleBadRequest(final PersistenceException ex, final WebRequest request) {
		logger.error("PersistenceException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/**
	 * This method is use to handle bad request.
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */
	@ExceptionHandler({ MaxSizeExceedException.class })
	public ResponseEntity<Object> handleBadRequest(final MaxSizeExceedException ex, final WebRequest request) {
		logger.error("MaxSizeExceedException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/**
	 * This method is use to handle bad request.
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */
	@ExceptionHandler({ MobileNotVerifiedException.class })
	public ResponseEntity<Object> handleBadRequest(final MobileNotVerifiedException ex, final WebRequest request) {
		logger.error("MobileNotVerifiedException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/**
	 * This method is use to handle bad request. 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler({ InsufficientBalanceException.class })
	protected ResponseEntity<Object> handleInsufficientBalanceException(final InsufficientBalanceException ex,
			final WebRequest request) {
		logger.error("InsufficientBalanceException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), Optional.empty());
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleMissingServletRequestParameter(org.springframework.web.bind.MissingServletRequestParameterException, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status,
			final WebRequest request) {
		logger.error("MissingServletRequestParameterException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleMethodArgumentNotValid(org.springframework.web.bind.MethodArgumentNotValidException, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		logger.error("MethodArgumentNotValidException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleHttpMessageNotWritable(org.springframework.http.converter.HttpMessageNotWritableException, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(final HttpMessageNotWritableException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		logger.error("HttpMessageNotWritableException: {}", ex);
		return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, ex.getMessage(), null);
	}

	/**
	 * This method is use for internal handle.
	 * @param ex
	 * @param request
	 * @return ResponseEntity
	 */

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleInternal(final Exception ex, final WebRequest request) {
		logger.error("500 Status Code");
		logger.error("Exception: {}", ex);
		return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, true, ex.getMessage(), null);
	}

}