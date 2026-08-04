package kr.co.mathrank.domain.problem.assessment.exception;

import kr.co.mathrank.common.exception.HttpMathRankException;

public class AssessmentAttemptException extends HttpMathRankException {
	private AssessmentAttemptException(final int httpStatusCode, final int code, final String message) {
		super(httpStatusCode, code, message);
	}

	public static AssessmentAttemptException notFound() {
		return new AssessmentAttemptException(404, 7013, "응시 기록을 찾을 수 없습니다.");
	}

	public static AssessmentAttemptException accessDenied() {
		return new AssessmentAttemptException(403, 7014, "다른 사용자의 응시 기록에는 접근할 수 없습니다.");
	}

	public static AssessmentAttemptException answerLocked() {
		return new AssessmentAttemptException(409, 7015, "아직 답안을 입력할 수 없습니다.");
	}

	public static AssessmentAttemptException expired() {
		return new AssessmentAttemptException(409, 7016, "시험 시간이 만료되었습니다.");
	}

	public static AssessmentAttemptException alreadySubmitted() {
		return new AssessmentAttemptException(409, 7017, "이미 제출된 응시 기록입니다.");
	}

	public static AssessmentAttemptException attemptRequired() {
		return new AssessmentAttemptException(409, 7018, "시험 시작 후 응시 기록을 통해 제출해야 합니다.");
	}

	public static AssessmentAttemptException invalidAnswer() {
		return new AssessmentAttemptException(400, 7019, "제출 답안 형식이 올바르지 않습니다.");
	}
}
