package kr.co.mathrank.domain.problem.assessment.dto;

import kr.co.mathrank.domain.problem.core.Difficulty;

public record AssessmentQuery(
	String assessmentName,
	Difficulty difficulty
) {
}
