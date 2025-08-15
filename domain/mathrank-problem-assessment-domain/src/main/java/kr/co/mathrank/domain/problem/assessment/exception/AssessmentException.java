package kr.co.mathrank.domain.problem.assessment.exception;

import kr.co.mathrank.common.exception.MathRankException;

public class AssessmentException extends MathRankException {
	public AssessmentException(int code, String message) {
		super(code, message);
	}
}
