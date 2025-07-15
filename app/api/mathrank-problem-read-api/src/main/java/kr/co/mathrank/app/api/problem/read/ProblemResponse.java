package kr.co.mathrank.app.api.problem.read;

import java.time.LocalDateTime;

import kr.co.mathrank.client.external.school.SchoolInfo;
import kr.co.mathrank.client.internal.member.MemberInfo;
import kr.co.mathrank.domain.problem.dto.ProblemQueryResult;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;

public record ProblemResponse(
	Long id,
	MemberResponse memberInfo,
	String imageSource,
	Difficulty difficulty,
	AnswerType type,
	SchoolResponse schoolInfo,
	String answer,
	LocalDateTime createdAt
) {
	public static ProblemResponse from(
		final ProblemQueryResult result,
		final MemberInfo memberInfo,
		final SchoolInfo schoolInfo
	) {
		return new ProblemResponse(
			result.id(),
			MemberResponse.from(memberInfo),
			result.imageSource(),
			result.difficulty(),
			result.type(),
			SchoolResponse.from(schoolInfo),
			result.answer(),
			result.createdAt()
		);
	}
}
