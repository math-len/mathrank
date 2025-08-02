package kr.co.mathrank.domain.problem.single.exception;

public class CannotRegisterWithThisRoleException extends SingleProblemException {
	public CannotRegisterWithThisRoleException() {
		super(5001, "개별문제 등록 권한 부재");
	}
}
