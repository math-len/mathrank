package kr.co.mathrank.app.api.course.inner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.mathrank.domain.course.dto.CourseQueryContainsParentsResult;
import kr.co.mathrank.domain.course.service.CourseQueryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CourseInnerController {
	private final CourseQueryService courseQueryService;

	@Operation(hidden = true)
	@GetMapping("/api/inner/v1/course/parents")
	public ResponseEntity<CourseQueryContainsParentsResult> queryAllParents(@RequestParam final String coursePath) {
		return ResponseEntity.ok(courseQueryService.queryParents(coursePath));
	}
}
