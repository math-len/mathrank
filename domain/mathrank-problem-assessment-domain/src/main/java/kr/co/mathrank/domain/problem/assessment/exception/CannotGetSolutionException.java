package kr.co.mathrank.domain.problem.assessment.exception;

public class CannotGetSolutionException extends AssessmentException {
	public CannotGetSolutionException() {
		super(7012, "문제집의 정답을 조회할 수 없습니다.");
	}
}
