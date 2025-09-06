package kr.co.mathrank.app.api.problem.read;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.read.dto.ProblemReadQuery;

public record ProblemQueryRequest(
	@NotNull
	Boolean mine,
	Long problemId,
	Difficulty difficultyMinInclude,
	Difficulty difficultyMaxInclude,
	AnswerType answerType,
	String coursePath,
	Boolean videoExist,
	Integer year,
	String location,
	String schoolCode
) {
	public ProblemReadQuery toQuery(final Long requestMemberId) {
		return new ProblemReadQuery(
			// true -> 본인 아이디로 지정한다.
			// false -> null 로 지정한다.
			mine ? requestMemberId : null,
			problemId,
			difficultyMinInclude,
			difficultyMaxInclude,
			answerType,
			coursePath,
			videoExist,
			year,
			location,
			schoolCode
		);
	}
}
