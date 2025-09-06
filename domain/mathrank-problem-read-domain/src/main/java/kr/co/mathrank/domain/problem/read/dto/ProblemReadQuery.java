package kr.co.mathrank.domain.problem.read.dto;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.dto.ProblemQuery;

public record ProblemReadQuery(
	Long memberId,
	Long problemId,
	Difficulty difficultyMinInclude,
	Difficulty difficultyMaxInclude,
	AnswerType answerType,
	String path,
	Boolean solutionVideoExist,
	Integer year,
	String location,
	String schoolCode
) {
	public ProblemQuery toQuery() {
		return new ProblemQuery(
			memberId,
			problemId,
			difficultyMinInclude,
			difficultyMaxInclude,
			answerType,
			path,
			solutionVideoExist,
			year,
			location,
			schoolCode
		);
	}
}
