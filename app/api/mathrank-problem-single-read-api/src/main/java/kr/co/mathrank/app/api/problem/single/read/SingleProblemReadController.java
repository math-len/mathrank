package kr.co.mathrank.app.api.problem.single.read;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.client.internal.course.CourseClient;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelResult;
import kr.co.mathrank.domain.problem.single.read.entity.OrderColumn;
import kr.co.mathrank.domain.problem.single.read.entity.OrderDirection;
import kr.co.mathrank.domain.problem.single.read.service.SingleProblemQueryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "개별 문제 API")
@RestController
@RequiredArgsConstructor
public class SingleProblemReadController {
	private final SingleProblemQueryService singleProblemQueryService;
	private final CourseClient courseClient;

	@Operation(summary = "단일 개별문제 조회 API")
	@GetMapping("/api/v1/problem/single/{singleProblemId}")
	public ResponseEntity<SingleProblemReadModelResponse> getDetail(
		@LoginInfo final MemberPrincipal memberPrincipal,
		@PathVariable final Long singleProblemId
	) {
		final SingleProblemReadModelResult result = singleProblemQueryService.getProblemWithSolverStatus(
			singleProblemId,
			memberPrincipal == null ? null : memberPrincipal.memberId() // 로그인 안된 사용자면 Null
		);
		final SingleProblemReadModelResponse response = SingleProblemReadModelResponse.of(
			result,
			courseClient.getParentCourses(result.coursePath())
		);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/api/v1/problem/single")
	@Operation(summary = "풀이 시도 가능한 개별문제 페이징 조회 API", description = "정렬 기준 설정하지 않으면, 날짜 최신순 조회가 기본으로 사용됩니다.")
	public ResponseEntity<PageResult<SingleProblemReadModelResponse>> getSingleProblems(
		@LoginInfo final MemberPrincipal memberPrincipal,
		@ModelAttribute @ParameterObject final SingleProblemQueryRequest query,
		@RequestParam(required = false) final OrderColumn orderColumn,
		@RequestParam(required = false) final OrderDirection direction,
		@Range(min = 1, max = 1000) @RequestParam(defaultValue = "1") final Integer pageNumber,
		@Range(min = 1, max = 20) @RequestParam(defaultValue = "1") final Integer pageSize
	) {
		final PageResult<SingleProblemReadModelResult> result = singleProblemQueryService.queryPage(
			query.toQuery(),
			orderColumn,
			direction,
			memberPrincipal == null ? null : memberPrincipal.memberId(), // 로그인 안된 사용자면 Null
			pageSize,
			pageNumber
		);
		return ResponseEntity.ok(result.map(this::mapToResponse));
	}

	private SingleProblemReadModelResponse mapToResponse(final SingleProblemReadModelResult modelResult) {
		return SingleProblemReadModelResponse.of(modelResult, courseClient.getParentCourses(modelResult.coursePath()));
	}
}
