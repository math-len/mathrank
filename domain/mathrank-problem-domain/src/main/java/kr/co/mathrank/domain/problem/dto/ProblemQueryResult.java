package kr.co.mathrank.domain.problem.dto;

import java.time.LocalDateTime;
import java.util.Set;

import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;
import kr.co.mathrank.domain.problem.entity.Problem;

public record ProblemQueryResult(
	Long id,
	Long memberId,
	String imageSource,
	String path,
	Difficulty difficulty,
	AnswerType type,
	String schoolCode,
	Set<String> answer,
	LocalDateTime createdAt
) {
	public static ProblemQueryResult from(Problem problem) {
		return new ProblemQueryResult(
			problem.getId(),
			problem.getMemberId(),
			problem.getImageSource(),
			// null 대비
			problem.getCourse() == null ? "" : problem.getCourse().getPath().getPath(),
			problem.getDifficulty(),
			problem.getType(),
			problem.getSchoolCode(),
			problem.getAnswers(),
			problem.getCreatedAt()
		);
	}
}