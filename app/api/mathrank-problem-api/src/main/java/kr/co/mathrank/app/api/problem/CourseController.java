package kr.co.mathrank.app.api.problem;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.domain.problem.dto.CourseQueryResult;
import kr.co.mathrank.domain.problem.service.CourseQueryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "과정 API", description = "문제 등록에서 사용되는 과정( 초/중/고, 대단원, 중단원, 소단원 )을 조회하기 위한 API 입니다.")
@RestController
@RequiredArgsConstructor
public class CourseController {
	private final CourseQueryService courseQueryService;

	@Operation(summary = "다음 단계의 과정을 조회합니다.", description = "depth 가 정확히 \"1\" 높은 과정들만 조회합니다. ex) 초1 조회 시 -> {01 다항식, 02 방정식, ..., 04 도형의 방정식}")
	@GetMapping("/api/v1/problem/course")
	@Authorization(openedForAll = true)
	public ResponseEntity<List<CourseQueryResult>> getChildes(
		@RequestParam(defaultValue = "", required = false) final String path
	) {
		return ResponseEntity.ok(courseQueryService.queryChildes(path).results());
	}
}
