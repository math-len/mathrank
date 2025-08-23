package kr.co.mathrank.app.api.problem.assessment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSubmissionQueryResult;
import kr.co.mathrank.domain.problem.assessment.service.SubmissionQueryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "문제집 API")
@RestController
@RequiredArgsConstructor
public class AssessmentReadController {
	private final SubmissionQueryService submissionQueryService;

	@Operation(summary = "제출된 답안지 채점 상태 조회 API")
	@Authorization(openedForAll = true)
	@GetMapping("/api/v1/problem/assessment/submission")
	public ResponseEntity<AssessmentSubmissionQueryResult> querySubmissionResult(
		@RequestParam final Long submissionId
	) {
		return ResponseEntity.ok(submissionQueryService.getSubmissionResult(submissionId));
	}
}
