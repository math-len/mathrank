package kr.co.mathrank.app.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ApiExceptionHandler {
	private static final HttpStatus API_EXCEPTION_STATUS = HttpStatus.BAD_REQUEST;

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiExceptionBody> handleValidationException(final ConstraintViolationException exception) {
		return ResponseEntity.status(API_EXCEPTION_STATUS)
			.body(ApiExceptionBody.of(1000, exception.getMessage()));
	}
}
