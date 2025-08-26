package kr.co.mathrank.domain.problem.assessment.read.dto;

import java.time.LocalDateTime;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;

public record AssessmentItemReadModelDetailResult(
	Long problemId,
	String problemImage,
	Long memberId,
	String path,
	CourseDetailResult courseDetailResult,
	Difficulty difficulty,
	AnswerType type,
	String schoolCode,
	LocalDateTime createdAt,
	Integer year
) {
}
