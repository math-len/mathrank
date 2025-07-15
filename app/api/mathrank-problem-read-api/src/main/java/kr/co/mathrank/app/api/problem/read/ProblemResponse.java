package kr.co.mathrank.app.api.problem.read;

import java.time.LocalDateTime;

import kr.co.mathrank.client.external.school.SchoolInfo;
import kr.co.mathrank.domain.problem.dto.ProblemQueryResult;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;

public record ProblemResponse(
	Long id,
	Long memberId,
	String userName,
	String imageSource,
	Difficulty difficulty,
	AnswerType type,
	String schoolCode,
	String schoolName,
	String schoolKind,
	String schoolCity,
	String answer,
	LocalDateTime createdAt
) {
	public static ProblemResponse from(
		final ProblemQueryResult result,
		final String userName,
		final SchoolInfo schoolInfo
	) {
		return new ProblemResponse(
			result.id(),
			result.memberId(),
			userName,
			result.imageSource(),
			result.difficulty(),
			result.type(),
			result.schoolCode(),
			schoolInfo.SCHUL_NM(),
			schoolInfo.SCHUL_KND_SC_NM(),
			schoolInfo.LCTN_SC_NM(),
			result.answer(),
			result.createdAt()
		);
	}
}
