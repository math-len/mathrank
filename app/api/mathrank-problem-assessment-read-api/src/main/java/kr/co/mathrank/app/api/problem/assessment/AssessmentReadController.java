package kr.co.mathrank.app.api.problem.assessment;

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
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDetailReadModelResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentQuery;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionQueryResult;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrderDirection;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrder;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentDetailReadService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentQueryService;
import kr.co.mathrank.domain.problem.assessment.service.SubmissionQueryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "문제집 API")
@RestController
@RequiredArgsConstructor
public class AssessmentReadController {
	private final AssessmentQueryService assessmentQueryService;
	private final SubmissionQueryService submissionQueryService;
	private final AssessmentDetailReadService assessmentDetailReadService;

	@Operation(summary = "제출된 답안지 채점 상태 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/assessment/submission")
	public ResponseEntity<AssessmentSubmissionQueryResult> querySubmissionResult(
		@RequestParam final Long submissionId
	) {
		return ResponseEntity.ok(submissionQueryService.getSubmissionResult(submissionId));
	}

	@Operation(summary = "문제집 페이지 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/assessment")
	public ResponseEntity<PageResult<AssessmentQueryResult>> queryPage(
		@ModelAttribute @ParameterObject final AssessmentQuery assessmentQuery,
		@RequestParam final AssessmentOrder order,
		@RequestParam final AssessmentOrderDirection direction,
		@RequestParam(defaultValue = "10") @Range(min = 1, max = 20) final Integer pageSize,
		@RequestParam(defaultValue = "1") @Range(min = 1, max = 1000) final Integer pageNumber
	) {
		return ResponseEntity.ok(assessmentQueryService.pageQuery(assessmentQuery, order, direction, pageSize, pageNumber));
	}

	@Operation(summary = "문제집 상세 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/assessment/{assessmentId}")
	public ResponseEntity<AssessmentDetailReadModelResult> getDetail(@PathVariable final Long assessmentId) {
		return ResponseEntity.ok(assessmentDetailReadService.getDetail(assessmentId));
	}
}
