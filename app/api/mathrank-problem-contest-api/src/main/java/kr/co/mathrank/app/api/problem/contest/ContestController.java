package kr.co.mathrank.app.api.problem.contest;

import org.springdoc.core.annotations.ParameterObject;
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
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentDeleteCommand;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSolutionQuery;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentSolutionQueryResult;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentUpdateCommand;
import kr.co.mathrank.domain.problem.assessment.dto.LimitedAssessmentRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.dto.SubmissionRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentDeleteService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentRegisterService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentSolutionQueryService;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentUpdateService;
import kr.co.mathrank.domain.problem.assessment.service.SubmissionRegisterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "대회 API")
public class ContestController {
	private final AssessmentRegisterService assessmentRegisterService;
	private final SubmissionRegisterService submissionRegisterService;
	private final AssessmentUpdateService assessmentUpdateService;
	private final AssessmentDeleteService assessmentDeleteService;
	private final AssessmentSolutionQueryService assessmentSolutionQueryService;

	@Operation(summary = "대회 등록", description = "문제집 등록은 관리자만 가능합니다.")
	@PostMapping("/api/v1/problem/contest")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> registerContest(
		@RequestBody @Valid final Requests.ContestRegisterRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final LimitedAssessmentRegisterCommand command = request.toCommand(memberPrincipal.memberId(), memberPrincipal.role());
		assessmentRegisterService.registerLimited(command);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "대회 답안지 등록 API")
	@PostMapping("/api/v1/problem/contest/submission")
	@Authorization(openedForAll = true)
	public ResponseEntity<String> registerSubmission(
		@RequestBody @Valid final Requests.ContestSubmissionRegisterRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final SubmissionRegisterCommand command = request.toCommand(memberPrincipal.memberId());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(String.valueOf(submissionRegisterService.submit(command)));
	}

	@Operation(summary = "대회 수정 API")
	@PutMapping("/api/v1/problem/contest/{contestId}")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> updateContest(
		@ModelAttribute @ParameterObject @Valid final Requests.ContestUpdateRequest request
	) {
		final AssessmentUpdateCommand command = request.toCommand();
		assessmentUpdateService.update(command);

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "대회 삭제 API")
	@DeleteMapping("/api/v1/problem/contest/{contestId}")
	@Authorization(openedForAll = true)
	public ResponseEntity<Void> delete(
		@PathVariable final Long contestId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final AssessmentDeleteCommand command = new AssessmentDeleteCommand(contestId, memberPrincipal.memberId(), memberPrincipal.role());
		assessmentDeleteService.delete(command);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "대회 정답 조회 API", description = "대회의 정답을 조회합니다. 이미 푼 시험지의 정답만 조회 가능합니다.")
	@GetMapping("/api/v1/problem/contest/{contestId}/solution")
	@Authorization(openedForAll = true)
	public ResponseEntity<AssessmentSolutionQueryResult> querySolution(
		@PathVariable final Long contestId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final AssessmentSolutionQuery query = new AssessmentSolutionQuery(contestId, memberPrincipal.memberId(), memberPrincipal.role());
		final AssessmentSolutionQueryResult result = assessmentSolutionQueryService.querySolutions(query);
		return ResponseEntity.ok(result);
	}
}
