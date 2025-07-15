package kr.co.mathrank.app.api.common.exception;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.ConstraintViolationException;
import kr.co.mathrank.common.exception.MathRankException;

@RestController
public class ApiExceptionOccuringController {
	@GetMapping("/error")
	public ResponseEntity<Void> occursError() {
		throw new ConstraintViolationException(Collections.emptySet());
	}

	@GetMapping("/require-argument")
	public ResponseEntity<Void> requireArgument(
		@RequestParam final String name
	) {
		return ResponseEntity.ok().build();
	}

	@GetMapping("/internal-exception")
	public ResponseEntity<Void> internalException() {
		throw new IllegalArgumentException();
	}

	@GetMapping("/mathrank-exception")
	public ResponseEntity<Void> mathRankException() {
		throw new MathRankException(11, "test");
	}
}
