package com.telino.avp.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AvpCentralExceptionHandler extends ResponseEntityExceptionHandler {

	
	
	// 500
	@ExceptionHandler({ AvpExploitException.class })
	public ResponseEntity<Object> handleResourceNotFound(final RuntimeException ex, final WebRequest request) {
		logger.error("AVPExploitException - 500 Status Code", ex);
		
		
		
		
		return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
//
//	// 404
//	@ExceptionHandler({ UserNotFoundException.class })
//	public ResponseEntity<Object> handleUserNotFound(final RuntimeException ex, final WebRequest request) {
//		logger.error("404 Status Code", ex);
//		final GenericResponse bodyOfResponse = new GenericResponse(
//				messages.getMessage("message.userNotFound", null, request.getLocale()), ex.getMessage());
//		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
//	}
//
//	// 409
//	@ExceptionHandler({ UserAlreadyExistException.class })
//	public ResponseEntity<Object> handleUserAlreadyExist(final RuntimeException ex, final WebRequest request) {
//		logger.error("409 Status Code", ex);
//		final GenericResponse bodyOfResponse = new GenericResponse(
//				messages.getMessage("message.regError", null, request.getLocale()), "UserAlreadyExist");
//		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
//	}
//
//
//	@ExceptionHandler({ Exception.class })
//	public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
//		logger.error("500 Status Code", ex);
//		final GenericResponse bodyOfResponse = new GenericResponse(
//				messages.getMessage("message.error", null, request.getLocale()), "InternalError");
//		return new ResponseEntity<Object>(bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//	}
}
