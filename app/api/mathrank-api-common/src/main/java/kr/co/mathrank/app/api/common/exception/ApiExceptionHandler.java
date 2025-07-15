package kr.co.mathrank.app.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
	private static final HttpStatus API_EXCEPTION_STATUS = HttpStatus.BAD_REQUEST;
	private static final HttpStatus SERVER_EXCEPTION_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiExceptionBody> handleValidationException(final ConstraintViolationException exception) {
		log.warn("[ApiExceptionHandler] wrong arguments with: {}", exception.getConstraintViolations(), exception);
		return ResponseEntity.status(API_EXCEPTION_STATUS)
			.body(ApiExceptionBody.of(1000, exception.getMessage()));
	}

	@ExceptionHandler(ServletException.class)
	public ResponseEntity<ApiExceptionBody> handleServletException(final ServletException exception) {
		log.warn("[ApiExceptionHandler] wrong api access with: {}", exception.getMessage(), exception);
		return ResponseEntity.status(API_EXCEPTION_STATUS)
			.body(ApiExceptionBody.of(1001, exception.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiExceptionBody> handlerInternalException(final Exception exception) {
		log.error("[ApiExceptionHandler] internal exception: {}", exception.getMessage(), exception);
		return ResponseEntity.status(SERVER_EXCEPTION_STATUS)
			.body(ApiExceptionBody.of(9000, "서버 내 오류 발생"));
	}
}
