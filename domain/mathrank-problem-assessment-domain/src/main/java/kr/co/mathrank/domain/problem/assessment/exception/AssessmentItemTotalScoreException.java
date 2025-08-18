package kr.co.mathrank.domain.problem.assessment.exception;

public class AssessmentItemTotalScoreException extends AssessmentException {
	public AssessmentItemTotalScoreException() {
		super(7002, "문제집의 점수 총 합이 100이 아닙니다.");
	}
}
