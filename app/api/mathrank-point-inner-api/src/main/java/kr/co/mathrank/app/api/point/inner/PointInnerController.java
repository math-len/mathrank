package kr.co.mathrank.app.api.point.inner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.mathrank.domain.point.dto.PointQuery;
import kr.co.mathrank.domain.point.dto.PointQueryResult;
import kr.co.mathrank.domain.point.service.PointQueryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PointInnerController {
	private final PointQueryService pointQueryService;

	@GetMapping("/api/inner/v1/point")
	@Operation(hidden = true)
	public ResponseEntity<PointQueryResult> queryPoint(
		@RequestParam final Long memberId
	) {
		final PointQueryResult result = pointQueryService.queryPoint(new PointQuery(memberId));

		return ResponseEntity.ok(result);
	}
}
