package kr.co.mathrank.app.api.common.exception;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.ConstraintViolationException;

@RestController
public class ApiExceptionOccuringController {
	@GetMapping("/error")
	public ResponseEntity<Void> occursError() {
		throw new ConstraintViolationException(Collections.emptySet());
	}
}
