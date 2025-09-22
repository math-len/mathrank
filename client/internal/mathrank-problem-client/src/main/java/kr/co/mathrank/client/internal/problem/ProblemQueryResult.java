package kr.co.mathrank.client.internal.problem;

import java.time.LocalDateTime;
import java.util.Set;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.core.PastProblem;

public record ProblemQueryResult(
	Long id,
	Long memberId,
	String imageSource,
	String path,
	Difficulty difficulty,
	AnswerType type,
	PastProblem pastProblem,
	String schoolCode,
	Set<String> answer,
	LocalDateTime createdAt,
	Integer year,
	String solutionVideoLink,
	String solutionImage,
	String memo,
	String location
) {
}
