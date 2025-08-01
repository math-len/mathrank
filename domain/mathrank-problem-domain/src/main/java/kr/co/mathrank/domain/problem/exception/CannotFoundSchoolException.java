package kr.co.mathrank.domain.problem.exception;

public class CannotFoundSchoolException extends ProblemException {
  public CannotFoundSchoolException() {
    super(3003, "학교를 찾을 수 없습니다.");
  }
}
