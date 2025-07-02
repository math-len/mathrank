package kr.co.mathrank.domain.problem.dto;

import java.time.LocalDateTime;

import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.Problem;
import kr.co.mathrank.domain.problem.entity.ProblemCourse;

public record ProblemQueryResult(
	Long id,
	Long memberId,
	String imageSource,
	Difficulty difficulty,
	ProblemCourse problemCourse,
	AnswerType type,
	String schoolCode,
	String answer,
	LocalDateTime createdAt
) {
	public static ProblemQueryResult from(Problem problem) {
		return new ProblemQueryResult(
			problem.getId(),
			problem.getMemberId(),
			problem.getImageSource(),
			problem.getDifficulty(),
			problem.getProblemCourse(),
			problem.getType(),
			problem.getSchoolCode(),
			problem.getAnswer(),
			problem.getCreatedAt()
		);
	}
}