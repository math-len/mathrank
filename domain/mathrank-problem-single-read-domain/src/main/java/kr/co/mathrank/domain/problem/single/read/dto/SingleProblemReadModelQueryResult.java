package kr.co.mathrank.domain.problem.single.read.dto;

import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.entity.SingleProblemReadModel;

public record SingleProblemReadModelQueryResult(
	Long id, // single problem id
	Boolean successAtFirstTry, // null: 푼적 없음, true: 성공해썽, false: 실패해썽
	Long problemId, // problem id
	String singleProblemName,
	String problemImage,
	CourseContainsParentResult courseInfo,
	String location,
	String schoolCode,
	AnswerType answerType,
	Difficulty difficulty,
	Long firstTrySuccessCount, // 첫번째 시도에서 성공한 횟수
	Long totalAttemptedCount, // 문제를 풀려고 시도한 총 횟수
	Long attemptedUserDistinctCount, // 해당 문제를 풀려고 한 사용자 수
	Double accuracy // 정답률
) {
	public static SingleProblemReadModelQueryResult from(
		final SingleProblemReadModel model,
		final CourseContainsParentResult courseContainsParentResult,
		final Boolean successAtFirstTry
	) {
		return new SingleProblemReadModelQueryResult(
			model.getId(),
			successAtFirstTry,
			model.getProblemId(),
			model.getSingleProblemName(),
			model.getProblemImage(),
			courseContainsParentResult,
			model.getLocation(),
			model.getSchoolCode(),
			model.getAnswerType(),
			model.getDifficulty(),
			model.getFirstTrySuccessCount(),
			model.getTotalAttemptedCount(),
			model.getAttemptedUserDistinctCount(),
			model.getAccuracy()
		);
	}

	public static SingleProblemReadModelQueryResult from(
		final SingleProblemReadModelResult model,
		final CourseContainsParentResult courseQueryResult
	) {
		return new SingleProblemReadModelQueryResult(
			model.id(),
			model.successAtFirstTry(),
			model.problemId(),
			model.singleProblemName(),
			model.problemImage(),
			courseQueryResult,
			model.location(),
			model.schoolCode(),
			model.answerType(),
			model.difficulty(),
			model.firstTrySuccessCount(),
			model.totalAttemptedCount(),
			model.attemptedUserDistinctCount(),
			model.accuracy()
		);
	}
}
