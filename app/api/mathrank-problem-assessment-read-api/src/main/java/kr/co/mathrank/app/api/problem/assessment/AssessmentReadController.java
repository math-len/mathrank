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
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.common.page.PageResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDetailReadModelResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentQuery;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentQueryResult;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrder;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrderDirection;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentDetailReadService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentQueryService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentRankQueryService;
import kr.co.mathrank.domain.problem.assessment.service.SubmissionQueryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "문제집 API")
@RestController
@RequiredArgsConstructor
public class AssessmentReadController {
	private final AssessmentQueryService assessmentQueryService;
	private final SubmissionQueryService submissionQueryService;
	private final AssessmentDetailReadService assessmentDetailReadService;
	private final AssessmentRankQueryService assessmentRankQueryService;

	@Operation(summary = "제출된 답안지 채점 상태 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/assessment/submission/{submissionId}")
	public ResponseEntity<Responses.AssessmentSubmissionQueryResponse> querySubmissionResult(
		@PathVariable final Long submissionId
	) {
		final Responses.AssessmentSubmissionQueryResponse response = Responses.AssessmentSubmissionQueryResponse.from(
			submissionQueryService.getSubmissionResult(submissionId));
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "제출 이력 확인 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/assessment/{assessmentId}/submission")
	public ResponseEntity<Responses.AssessmentSubmissionQueryResponses> querySubmissionResults(
		@PathVariable final Long assessmentId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final Responses.AssessmentSubmissionQueryResponses responses = Responses.AssessmentSubmissionQueryResponses.from(
			submissionQueryService.getAssessmentSubmissionResults(assessmentId, memberPrincipal.memberId()));
		return ResponseEntity.ok(responses);
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

	@Operation(summary = "문제집 랭크 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/assessment/submission/{submissionId}/rank")
	public ResponseEntity<Responses.AssessmentSubmissionRankResponse> getSubmissionRank(
		@PathVariable final Long submissionId
	) {
		Responses.AssessmentSubmissionRankResponse response = Responses.AssessmentSubmissionRankResponse.from(
			assessmentRankQueryService.getRank(submissionId));
		return ResponseEntity.ok(response);
	}
}
