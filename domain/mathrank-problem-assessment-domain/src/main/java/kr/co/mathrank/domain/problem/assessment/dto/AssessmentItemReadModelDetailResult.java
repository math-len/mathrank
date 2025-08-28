package kr.co.mathrank.domain.problem.assessment.dto;

import java.time.LocalDateTime;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;

public record AssessmentItemReadModelDetailResult(
	Long problemId,
	String problemImage,
	Long memberId,
	CourseDetailResult courseDetailResult,
	Difficulty difficulty,
	AnswerType type,
	String schoolCode,
	LocalDateTime createdAt,
	Integer year
) {
}
