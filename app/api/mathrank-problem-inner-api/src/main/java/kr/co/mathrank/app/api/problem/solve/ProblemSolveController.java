package kr.co.mathrank.app.api.problem.solve;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.mathrank.domain.problem.dto.ProblemSolveCommand;
import kr.co.mathrank.domain.problem.dto.ProblemSolveResult;
import kr.co.mathrank.domain.problem.exception.CannotFoundProblemException;
import kr.co.mathrank.domain.problem.service.ProblemQueryService;
import kr.co.mathrank.domain.problem.service.ProblemSolveService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProblemSolveController {
	private final ProblemSolveService problemSolveService;
	private final ProblemQueryService problemQueryService;

	@Operation(hidden = true)
	@GetMapping(value = "/api/inner/v1/problem/solve")
	public ResponseEntity<ProblemSolveResult> isMatch(
		@RequestParam final Long problemId,
		@RequestParam final List<String> answers
	) {
		final ProblemSolveResult result = problemSolveService.solve(new ProblemSolveCommand(problemId, answers));
		return ResponseEntity.ok(result);
	}

	/**
	 * 문제가 존재하는지 확인
	 * @param problemId
	 * @return
	 */
	@Operation(hidden = true)
	@RequestMapping(method = RequestMethod.HEAD, value = "/api/inner/v1/problem")
	public ResponseEntity<Void> isExist(
		@RequestParam final Long problemId
	) {
		try {
			problemQueryService.getSingle(problemId);
			return ResponseEntity.ok().build();
		} catch (CannotFoundProblemException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
