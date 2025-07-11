package kr.co.mathrank.app.api.problem;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.domain.problem.dto.CourseQueryResult;
import kr.co.mathrank.domain.problem.service.CourseQueryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CourseController {
	private final CourseQueryService courseQueryService;

	@GetMapping("/api/v1/problem/course")
	@Authorization(openedForAll = true)
	public ResponseEntity<List<CourseQueryResult>> getChildes(
		@RequestParam(defaultValue = "", required = false) final String path
	) {
		return ResponseEntity.ok(courseQueryService.queryChildes(path).results());
	}
}
