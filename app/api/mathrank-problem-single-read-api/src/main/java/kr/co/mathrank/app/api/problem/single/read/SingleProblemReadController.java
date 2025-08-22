package kr.co.mathrank.app.api.problem.single.read;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.client.internal.course.CourseClient;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelPageResult;
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

	@GetMapping("/api/v1/problem/single")
	@Operation(summary = "풀이 시도 가능한 개별문제 페이징 조회 API", description = "정렬 기준 설정하지 않으면, 날짜 최신순 조회가 기본으로 사용됩니다.")
	@Authorization(openedForAll = true)
	public ResponseEntity<SingleProblemQueryPageResponse> getSingleProblems(
		@LoginInfo final MemberPrincipal memberPrincipal,
		@ModelAttribute @ParameterObject final SingleProblemQueryRequest query,
		@RequestParam(required = false) final OrderColumn orderColumn,
		@RequestParam(required = false) final OrderDirection direction,
		@Range(min = 1, max = 1000) @RequestParam(defaultValue = "1") final Integer pageNumber,
		@Range(min = 1, max = 20) @RequestParam(defaultValue = "1") final Integer pageSize
	) {
		final SingleProblemReadModelPageResult result = singleProblemQueryService.queryPage(
			query.toQuery(),
			orderColumn,
			direction,
			memberPrincipal.memberId(),
			pageSize,
			pageNumber
		);

		// course 정보 덧붙이기!
		final List<SingleProblemReadModelResponse> singleProblemResponse = mergeCourseInfos(result);

		final SingleProblemQueryPageResponse response = SingleProblemQueryPageResponse.from(
			singleProblemResponse,
			result.currentPageNumber(),
			result.currentPageSize(),
			result.possibleNextPageNumbers()
		);
		return ResponseEntity.ok(response);
	}

	private List<SingleProblemReadModelResponse> mergeCourseInfos(SingleProblemReadModelPageResult result) {
		return result.queryResults().stream()
			.map(modelResult -> SingleProblemReadModelResponse.of(modelResult, courseClient.getParentCourses(modelResult.coursePath())))
			.toList();
	}
}
