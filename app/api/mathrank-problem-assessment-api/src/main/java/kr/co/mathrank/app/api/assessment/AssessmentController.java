package kr.co.mathrank.app.api.assessment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.assessment.dto.AssessmentRegisterCommand;
import kr.co.mathrank.domain.problem.assessment.service.AssessmentRegisterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "문제집 API")
public class AssessmentController {
	private final AssessmentRegisterService assessmentRegisterService;

	@Operation(summary = "문제집 등록", description = "문제집 등록은 관리자만 가능합니다.")
	@PostMapping("/api/v1/problem/assessment")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> registerAssessment(
		@RequestBody @Valid final Requests.AssessmentRegisterRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final AssessmentRegisterCommand command = request.toCommand(memberPrincipal.memberId(), memberPrincipal.role());
		assessmentRegisterService.register(command);

		return ResponseEntity.ok().build();
	}
}
