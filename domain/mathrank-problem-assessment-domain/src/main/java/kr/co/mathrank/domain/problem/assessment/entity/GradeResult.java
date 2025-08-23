package kr.co.mathrank.domain.problem.assessment.entity;

import java.util.List;

public record GradeResult(
	Long problemId,
	List<String> correctAnswer,
	Boolean success
) {
}
