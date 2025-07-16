package kr.co.mathrank.app.api.problem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "문제 API")
public class ProblemController {
	private final ProblemService problemService;

	@Operation(summary = "문제 등록 API", description = "이 API를 통해 등록된 문제들은, 서버내의 다른 API ( 개별문제, 시험문제 )에서 조합하여 사용할 수 있습니다.")
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

	@Operation(summary = "문제 수정 API", description = "문제를 수정합니다. 바꾸지 않고자 하는 필드가 있다면, 해당 필드를 원본 값으로 채워야합니다.")
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

	@Operation(summary = "문제 삭제 API", description = "다른 도메인에서 사용된 문제는 모두 무효화 됩니다.")
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
