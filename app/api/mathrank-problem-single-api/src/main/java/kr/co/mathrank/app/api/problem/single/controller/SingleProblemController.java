package kr.co.mathrank.app.api.problem.single.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRegisterCommand;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveCommand;
import kr.co.mathrank.domain.problem.single.service.SingleProblemService;
import lombok.RequiredArgsConstructor;

@Tag(name = "개별 문제 API")
@RestController
@RequiredArgsConstructor
public class SingleProblemController {
	private final SingleProblemService singleProblemService;

	@Operation(summary = "개별 문제 풀이 API", description = "해당 사용자의 문제풀이를 채점하고, 결과를 저장합니다. 풀이 결과는 모두 저장됩니다")
	@PostMapping("/api/v1/problem/single/solve")
	@Authorization(openedForAll = true)
	public ResponseEntity<Void> solveSingleProblem(
		@ModelAttribute @ParameterObject @Valid final SingleProblemSolveRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final SingleProblemSolveCommand command = request.toCommand(memberPrincipal.memberId());
		singleProblemService.solve(command);

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "개별 문제 등록 API", description = "특정 문제를 개별문제로 등록합니다. 문제 중복 등록 시도시, 거부됩니다.")
	@PostMapping("/api/v1/problem/single")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> registerProblem(
		@ModelAttribute @ParameterObject @Valid final SingleProblemRegisterRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final SingleProblemRegisterCommand command = request.toCommand(memberPrincipal.memberId(),
			memberPrincipal.role());
		singleProblemService.register(command);
		return ResponseEntity.ok().build();
	}
}
