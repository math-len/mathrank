package kr.co.mathrank.app.api.problem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.dto.ProblemDeleteCommand;
import kr.co.mathrank.domain.problem.dto.ProblemRegisterCommand;
import kr.co.mathrank.domain.problem.dto.ProblemUpdateCommand;
import kr.co.mathrank.domain.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "problem")
public class ProblemController {
	private final ProblemService problemService;

	@PostMapping("/api/v1/problem")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> register(
		@RequestBody @Valid final Requests.ProblemRegisterRequest request,
		@LoginInfo final MemberPrincipal principal
	) {
		final ProblemRegisterCommand command = request.toCommand(principal.memberId());
		problemService.save(command);

		return ResponseEntity.ok().build();
	}

	@PutMapping("/api/v1/problem")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> update(
		@RequestBody @Valid final Requests.ProblemUpdateRequest request,
		@LoginInfo final MemberPrincipal principal
	) {
		final ProblemUpdateCommand command = request.toCommand(principal.memberId());
		problemService.update(command);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/api/v1/problem")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> delete(
		@RequestParam final Long problemId,
		@LoginInfo final MemberPrincipal principal
	) {
		problemService.delete(new ProblemDeleteCommand(problemId, principal.memberId()));

		return ResponseEntity.ok().build();
	}
}
