package kr.co.mathrank.domain.problem.dto;

import java.util.List;
import java.util.Set;

public record ProblemSolveResult(
	Boolean success,
	List<String> submittedAnswer,
	Set<String> correctAnswer
) {
}
