package kr.co.mathrank.domain.problem.assessment.exception;

import kr.co.mathrank.common.exception.HttpMathRankException;

public class InvalidAssessmentAnswerInputDelayException extends HttpMathRankException {
	public InvalidAssessmentAnswerInputDelayException() {
		super(400, 7020, "답안 입력 잠금 시간은 0 이상이며 시험 시간보다 짧아야 합니다.");
	}
}
