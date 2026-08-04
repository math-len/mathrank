package kr.co.mathrank.app.api.assessment;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentAttemptResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDeleteCommand;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSolutionQuery;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSolutionQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentUpdateCommand;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentAttemptService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentDeleteService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentRegisterService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentSolutionQueryService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentUpdateService;
import kr.co.mathrank.domain.problem.assessment.service.SubmissionRegisterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "문제집 API")
public class AssessmentController {
	private final AssessmentRegisterService assessmentRegisterService;
	private final AssessmentAttemptService assessmentAttemptService;
	private final SubmissionRegisterService submissionRegisterService;
	private final AssessmentUpdateService assessmentUpdateService;
	private final AssessmentDeleteService assessmentDeleteService;
	private final AssessmentSolutionQueryService assessmentSolutionQueryService;

	@Operation(summary = "문제집 등록", description = "문제집 등록은 관리자만 가능합니다.")
	@PostMapping("/api/v1/problem/assessment")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Responses.AssessmentRegisterResponse> registerAssessment(
		@RequestBody @Valid final Requests.AssessmentRegisterRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final AssessmentRegisterCommand command = request.toCommand(memberPrincipal.memberId(), memberPrincipal.role());
		final Long assessmentId = assessmentRegisterService.register(command);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(Responses.AssessmentRegisterResponse.from(assessmentId));
	}

	@Operation(summary = "문제집 응시 시작 API", description = "서버 시각을 기준으로 응시를 시작하거나 진행 중 응시를 반환합니다.")
	@PostMapping("/api/v1/problem/assessment/{assessmentId}/attempts")
	@Authorization(openedForAll = true)
	public ResponseEntity<Responses.AssessmentAttemptResponse> startAttempt(
		@PathVariable final Long assessmentId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final AssessmentAttemptResult result = assessmentAttemptService.start(
			assessmentId, memberPrincipal.memberId());
		final HttpStatus status = result.newlyCreated() ? HttpStatus.CREATED : HttpStatus.OK;
		return ResponseEntity.status(status).body(Responses.AssessmentAttemptResponse.from(result));
	}

	@Operation(summary = "문제집 응시 답안 제출 API", description = "응시 시작 시각과 잠금·종료 시각을 서버에서 검증합니다.")
	@PostMapping("/api/v1/problem/assessment/attempts/{attemptId}/submission")
	@Authorization(openedForAll = true)
	public ResponseEntity<Responses.AssessmentAttemptSubmissionResponse> submitAttempt(
		@PathVariable final Long attemptId,
		@RequestBody @Valid final Requests.AssessmentAttemptSubmissionRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final Long submissionId = assessmentAttemptService.submit(
			request.toCommand(memberPrincipal.memberId(), attemptId));
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(Responses.AssessmentAttemptSubmissionResponse.from(submissionId));
	}

	@Operation(summary = "문제집 답안지 등록 API")
	@PostMapping("/api/v1/problem/assessment/submission")
	@Authorization(openedForAll = true)
	public ResponseEntity<String> registerSubmission(
		@RequestBody @Valid final Requests.AssessmentSubmissionRegisterRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final SubmissionRegisterCommand command = request.toCommand(memberPrincipal.memberId());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(String.valueOf(submissionRegisterService.submit(command)));
	}

	@Operation(summary = "문제집 수정 API")
	@PutMapping("/api/v1/problem/assessment/{assessmentId}")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> updateAssessment(
		@ModelAttribute @ParameterObject @Valid final Requests.AssessmentUpdateRequest request
	) {
		final AssessmentUpdateCommand command = request.toCommand();
		assessmentUpdateService.update(command);

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "문제집 삭제 API")
	@DeleteMapping("/api/v1/problem/assessment/{assessmentId}")
	@Authorization(openedForAll = true)
	public ResponseEntity<Void> delete(
		@PathVariable final Long assessmentId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final AssessmentDeleteCommand command = new AssessmentDeleteCommand(
			assessmentId,
			memberPrincipal.memberId(),
			memberPrincipal.role()
		);
		assessmentDeleteService.delete(command);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "문제집 정답 조회 API", description = "문제집의 정답을 조회합니다. 이미 푼 시험지의 정답만 조회 가능합니다.")
	@GetMapping("/api/v1/problem/assessment/{assessmentId}/solution")
	@Authorization(openedForAll = true)
	public ResponseEntity<AssessmentSolutionQueryResult> querySolution(
		@PathVariable final Long assessmentId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final AssessmentSolutionQuery query = new AssessmentSolutionQuery(
			assessmentId,
			memberPrincipal.memberId(),
			memberPrincipal.role()
		);
		final AssessmentSolutionQueryResult result = assessmentSolutionQueryService.querySolutions(query);
		return ResponseEntity.ok(result);
	}
}
