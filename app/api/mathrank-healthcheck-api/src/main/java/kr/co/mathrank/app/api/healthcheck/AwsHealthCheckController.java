package kr.co.mathrank.app.api.healthcheck;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class AwsHealthCheckController {

	@Operation(hidden = true)
	@GetMapping("/api/v1/health-check")
	public ResponseEntity<Void> healthCheck() {
		return ResponseEntity.ok().build();
	}
}
