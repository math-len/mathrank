package kr.co.mathrank.app.api.problem.read;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.problem.dto.ProblemQueryCommand;
import kr.co.mathrank.domain.problem.entity.AnswerType;
import kr.co.mathrank.domain.problem.entity.Difficulty;

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
	@NotNull
	@Range(min = 1, max = 20)
	Integer pageSize,
	@NotNull
	@Range(min = 1, max = 1000)
	Integer pageNumber
) {
	public ProblemQueryCommand toCommand(final Long requestMemberId) {
		return new ProblemQueryCommand(
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
			pageSize,
			pageNumber
		);
	}
}
