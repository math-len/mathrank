package kr.co.mathrank.app.api.problem.single.read;

import kr.co.mathrank.client.internal.course.CourseQueryContainsParentsResult;
import kr.co.mathrank.domain.problem.core.AnswerType;
import kr.co.mathrank.domain.problem.core.Difficulty;
import kr.co.mathrank.domain.problem.single.read.dto.SingleProblemReadModelResult;

public record SingleProblemReadModelResponse(
	Long id, // single problem id
	Boolean successAtFirstTry, // null: 푼적 없음, true: 성공해썽, false: 실패해썽
	Long problemId, // problem id
	String singleProblemName,
	String problemImage,

	CourseQueryContainsParentsResult courseInfo,

	AnswerType answerType,
	Difficulty difficulty,
	Long firstTrySuccessCount, // 첫번째 시도에서 성공한 횟수
	Long totalAttemptedCount, // 문제를 풀려고 시도한 총 횟수
	Long attemptedUserDistinctCount, // 해당 문제를 풀려고 한 사용자 수
	Double accuracy // 정답률
) {
	public static SingleProblemReadModelResponse of(SingleProblemReadModelResult result, CourseQueryContainsParentsResult courseInfo) {
		return new SingleProblemReadModelResponse(
			result.id(),
			result.successAtFirstTry(),
			result.problemId(),
			result.singleProblemName(),
			result.problemImage(),
			courseInfo,
			result.answerType(),
			result.difficulty(),
			result.firstTrySuccessCount(),
			result.totalAttemptedCount(),
			result.attemptedUserDistinctCount(),
			result.accuracy()
		);
	}
}
