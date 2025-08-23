package kr.co.mathrank.domain.problem.assessment.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.client.internal.problem.ProblemClient;
import kr.co.mathrank.client.internal.problem.SolveResult;
import kr.co.mathrank.domain.problem.assessment.entity.GradeResult;
import lombok.RequiredArgsConstructor;

@Component
@Validated
@RequiredArgsConstructor
class ItemGradeManager {
	private final ProblemClient problemClient;

	public GradeResult gradeItemSubmission(@NotNull final Long problemId, @NotNull final List<String> submittedAnswer) {
		final SolveResult solveResult = problemClient.matchAnswer(problemId, submittedAnswer);
		return create(solveResult, problemId);
	}

	private GradeResult create(final SolveResult solveResult, final Long problemId) {
		return new GradeResult(
			problemId,
			solveResult.realAnswer().stream().toList(),
			solveResult.success());
	}
}
