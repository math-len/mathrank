package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.LocalDateTime;
import java.util.Set;

import kr.co.mathrank.client.internal.problem.ProblemQueryResult;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;

public record ProblemSolutionResult(
	String imageSource,
	String path,
	Difficulty difficulty,
	AnswerType type,
	PastProblem pastProblem,
	Set<String> answer,
	LocalDateTime createdAt,
	Integer year,
	String solutionVideoLink,
	String solutionImage
) {
	public static ProblemSolutionResult from(final ProblemQueryResult result) {
		return new ProblemSolutionResult(
			result.imageSource(),
			result.path(),
			result.difficulty(),
			result.type(),
			result.pastProblem(),
			result.answer(),
			result.createdAt(),
			result.year(),
			result.solutionVideoLink(),
			result.solutionImage()
		);
	}
}