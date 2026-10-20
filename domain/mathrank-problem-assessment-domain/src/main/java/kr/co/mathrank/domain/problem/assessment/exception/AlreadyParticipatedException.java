package kr.co.mathrank.domain.problem.assessment.exception;

import kr.co.mathrank.common.exception.HttpMathRankException;

/**
 * 시험지·경시대회는 회원당 1회만 응시할 수 있다.
 * 기존에 여러 번 제출된 기록은 그대로 두고, 새 응시·제출만 막는다.
 */
public class AlreadyParticipatedException extends HttpMathRankException {
	private AlreadyParticipatedException(final int code, final String message) {
		super(409, code, message);
	}

	public static AlreadyParticipatedException assessment() {
		return new AlreadyParticipatedException(7021, "이미 응시한 시험지입니다.");
	}

	public static AlreadyParticipatedException contest() {
		return new AlreadyParticipatedException(7022, "이미 응시한 경시대회입니다.");
	}
}
