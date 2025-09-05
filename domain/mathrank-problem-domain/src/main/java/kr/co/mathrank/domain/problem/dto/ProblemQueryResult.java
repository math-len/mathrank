package kr.co.mathrank.domain.problem.dto;

import java.time.LocalDateTime;
import java.util.Set;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
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
	LocalDateTime createdAt,
	Integer year,
	String solutionVideoLink,
	String solutionImage,
	String memo,
	String location
) {
	public static ProblemQueryResult from(Problem problem) {
		return new ProblemQueryResult(
			problem.getId(),
			problem.getMemberId(),
			problem.getProblemImage(),
			// null 대비
			String.valueOf(problem.getCoursePath()),
			problem.getDifficulty(),
			problem.getType(),
			problem.getSchoolCode(),
			problem.getAnswers(),
			problem.getCreatedAt(),
			problem.getYears(),
			problem.getSolutionVideoLink(),
			problem.getSolutionImage(),
			problem.getMemo(),
			problem.getLocation()
		);
	}
}
