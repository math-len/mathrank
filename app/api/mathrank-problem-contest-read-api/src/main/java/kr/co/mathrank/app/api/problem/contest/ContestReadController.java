package kr.co.mathrank.app.api.problem.contest;

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
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDetailQuery;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrder;
import kr.co.mathrank.domain.problem.assessment.entity.AssessmentOrderDirection;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentDetailReadService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentQueryService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentRankQueryService;
import kr.co.mathrank.domain.problem.assessment.service.SubmissionQueryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "대회 API")
@RestController
@RequiredArgsConstructor
public class ContestReadController {
	private final AssessmentQueryService assessmentQueryService;
	private final SubmissionQueryService submissionQueryService;
	private final AssessmentDetailReadService assessmentDetailReadService;
	private final AssessmentRankQueryService assessmentRankQueryService;

	@Operation(summary = "제출된 답안지 채점 상태 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/contest/submission/{submissionId}")
	public ResponseEntity<Responses.ExamSubmissionQueryResponse> querySubmissionResult(
		@PathVariable final Long submissionId
	) {
		final Responses.ExamSubmissionQueryResponse response = Responses.ExamSubmissionQueryResponse.from(
			submissionQueryService.getSubmissionResult(submissionId));
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "제출 이력 확인 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/contest/{contestId}/submission")
	public ResponseEntity<Responses.ExamSubmissionQueryResponses> querySubmissionResults(
		@PathVariable final Long contestId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final Responses.ExamSubmissionQueryResponses responses = Responses.ExamSubmissionQueryResponses.from(
			submissionQueryService.getAssessmentSubmissionResults(contestId, memberPrincipal.memberId()));
		return ResponseEntity.ok(responses);
	}

	@Operation(summary = "대회 페이지 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/contest")
	public ResponseEntity<PageResult<Responses.ExamPageResponse>> queryPage(
		@ModelAttribute @ParameterObject final QueryRequests.ContestPageQueryRequest request,
		@RequestParam(required = false, defaultValue = "LATEST") final AssessmentOrder order,
		@RequestParam(required = false, defaultValue = "DESC") final AssessmentOrderDirection direction,
		@RequestParam(defaultValue = "10") @Range(min = 1, max = 20) final Integer pageSize,
		@RequestParam(defaultValue = "1") @Range(min = 1, max = 1000) final Integer pageNumber
	) {
		final PageResult<Responses.ExamPageResponse> pageResponses = assessmentQueryService.pageQuery(request.toQuery(),
				order, direction, pageSize, pageNumber)
			.map(Responses.ExamPageResponse::from);
		return ResponseEntity.ok(pageResponses);
	}

	@Operation(summary = "대회 상세 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/contest/{contestId}")
	public ResponseEntity<Responses.ExamDetailResponse> getDetail(@PathVariable final Long contestId) {
		return ResponseEntity.ok(Responses.ExamDetailResponse.from(assessmentDetailReadService.getDetail(
			AssessmentDetailQuery.periodLimited(contestId))));
	}

	@Operation(summary = "대회 랭크 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/contest/submission/{submissionId}/rank")
	public ResponseEntity<Responses.ExamSubmissionRankResponse> getSubmissionRank(
		@PathVariable final Long submissionId
	) {
		Responses.ExamSubmissionRankResponse response = Responses.ExamSubmissionRankResponse.from(
			assessmentRankQueryService.getRank(submissionId));
		return ResponseEntity.ok(response);
	}
}
