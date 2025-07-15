package kr.co.mathrank.app.api.problem.read;

import java.time.LocalDateTime;

import kr.co.mathrank.domain.problem.dto.ProblemQueryResult;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;

public record ProblemWithUserNameResult(
	Long id,
	Long memberId,
	String userName,
	String imageSource,
	Difficulty difficulty,
	AnswerType type,
	String schoolCode,
	String answer,
	LocalDateTime createdAt
) {
	public static ProblemWithUserNameResult from(
		final ProblemQueryResult result,
		final String userName
	) {
		return new ProblemWithUserNameResult(
			result.id(),
			result.memberId(),
			userName,
			result.imageSource(),
			result.difficulty(),
			result.type(),
			result.schoolCode(),
			result.answer(),
			result.createdAt()
		);
	}
}
