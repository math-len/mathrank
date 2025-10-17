package kr.co.mathrank.app.api.problem.single.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
import kr.co.mathrank.domain.problem.single.dto.SingleProblemChallengeLogResults;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemDeleteCommand;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRankQuery;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRankResult;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemRegisterCommand;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolutionQuery;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolutionQueryResult;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveCommand;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemSolveResult;
import kr.co.mathrank.domain.problem.single.dto.SingleProblemNameUpdateCommand;
import kr.co.mathrank.domain.problem.single.service.ChallengerQueryService;
import kr.co.mathrank.domain.problem.single.service.SingleProblemDeleteService;
import kr.co.mathrank.domain.problem.single.service.SingleProblemRankQueryService;
import kr.co.mathrank.domain.problem.single.service.SingleProblemService;
import kr.co.mathrank.domain.problem.single.service.SingleProblemSolutionQueryService;
import kr.co.mathrank.domain.problem.single.service.SingleProblemUpdateService;
import lombok.RequiredArgsConstructor;

@Tag(name = "개별 문제 API")
@RestController
@RequiredArgsConstructor
public class SingleProblemController {
	private final SingleProblemService singleProblemService;
	private final SingleProblemRankQueryService singleProblemRankQueryService;
	private final ChallengerQueryService challengerQueryService;
	private final SingleProblemDeleteService singleProblemDeleteService;
	private final SingleProblemSolutionQueryService singleProblemSolutionQueryService;
	private final SingleProblemUpdateService singleProblemUpdateService;

	@Operation(summary = "개별 문제 풀이 API", description = "해당 사용자의 문제풀이를 채점하고, 결과를 저장합니다. 풀이 결과는 모두 저장됩니다")
	@PostMapping("/api/v1/problem/single/solve")
	@Authorization(openedForAll = true)
	public ResponseEntity<SingleProblemSolveResponse> solveSingleProblem(
		@ModelAttribute @ParameterObject @Valid final SingleProblemSolveRequest request,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final SingleProblemSolveCommand command = request.toCommand(memberPrincipal.memberId());
		final SingleProblemSolveResult result = singleProblemService.solve(command);

		return ResponseEntity.ok(SingleProblemSolveResponse.from(result));
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

	@Operation(summary = "개별 문제 삭제 API", description = "개별문제를 삭제합니다. ADMIN 만 가능합니다.")
	@DeleteMapping("/api/v1/problem/single/{singleProblemId}")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> deleteSingleProblem(
		@PathVariable final Long singleProblemId
	) {
		singleProblemDeleteService.delete(new SingleProblemDeleteCommand(singleProblemId));
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "개별문제 랭크 조회 API")
	@GetMapping("/api/v1/problem/single/rank")
	@Authorization(openedForAll = true)
	public ResponseEntity<SingleProblemRankResponse> getRank(
		@RequestParam final Long singleProblemId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final SingleProblemRankResult result = singleProblemRankQueryService.queryFirstTriedLogRank(
			new SingleProblemRankQuery(memberPrincipal.memberId(), singleProblemId));
		final SingleProblemRankResponse response = SingleProblemRankResponse.from(result);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "개별문제 풀이 로그 ID 기반 랭크 조회 API")
	@GetMapping("/api/v1/problem/single/challenge-log/{challengeLogId}/rank")
	@Authorization(openedForAll = true)
	public ResponseEntity<SingleProblemRankResponse> getRank(
		@PathVariable final Long challengeLogId
	) {
		final SingleProblemRankResult result = singleProblemRankQueryService.queryRank(challengeLogId);
		final SingleProblemRankResponse response = SingleProblemRankResponse.from(result);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "개별문제 풀이 로그 조회 API ( 로그인된 사용자만 조회 )")
	@GetMapping("/api/v1/problem/single/{singleProblemId}/challenge-log")
	@Authorization(openedForAll = true)
	public ResponseEntity<List<SingleProblemChallengeLogResponse>> getSolveLogs(
		@PathVariable final Long singleProblemId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final SingleProblemChallengeLogResults results = challengerQueryService.findChallengeLogs(
			memberPrincipal.memberId(),
			singleProblemId);

		return ResponseEntity.ok(results.results().stream()
			.map(SingleProblemChallengeLogResponse::from)
			.toList());
	}

	@Operation(summary = "개별문제 정답 조회 API", description = "이미 문제를 푼 사용자만 조회 가능합니다.")
	@GetMapping("/api/v1/problem/single/{singleProblemId}/solution")
	@Authorization(openedForAll = true)
	public ResponseEntity<SingleProblemSolutionQueryResult> querySolution(
		@PathVariable final Long singleProblemId,
		@LoginInfo final MemberPrincipal memberPrincipal
	) {
		final SingleProblemSolutionQuery query = new SingleProblemSolutionQuery(singleProblemId, memberPrincipal.memberId());
		final SingleProblemSolutionQueryResult result = singleProblemSolutionQueryService.getSolution(query);

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "개별문제 이름 수정 API - 관리자 전용", description = "개별문제 이름을 수정합니다.")
	@PutMapping("/api/v1/problem/single/{singleProblemId}/title")
	@Authorization(values = Role.ADMIN)
	public ResponseEntity<Void> updateSingleProblemName(
		@PathVariable final Long singleProblemId,
		@RequestBody @Valid final SingleProblemNameUpdateRequest request
	) {
		final SingleProblemNameUpdateCommand command = request.toCommand(singleProblemId);
		singleProblemUpdateService.update(command);

		return ResponseEntity.ok().build();
	}
}
