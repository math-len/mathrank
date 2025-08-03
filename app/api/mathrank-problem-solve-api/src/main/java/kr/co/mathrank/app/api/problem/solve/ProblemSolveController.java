package kr.co.mathrank.app.api.problem.solve;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.mathrank.domain.problem.dto.ProblemSolveCommand;
import kr.co.mathrank.domain.problem.dto.ProblemSolveResult;
import kr.co.mathrank.domain.problem.service.ProblemSolveService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProblemSolveController {
	private final ProblemSolveService problemSolveService;

	@Operation(hidden = true)
	@GetMapping(value = "/api/inner/v1/problem/solve")
	public ResponseEntity<ProblemSolveResult> isMatch(
		@RequestParam final Long problemId,
		@RequestParam final List<String> answers
	) {
		final ProblemSolveResult result = problemSolveService.solve(new ProblemSolveCommand(problemId, answers));
		return ResponseEntity.ok(result);
	}
}
