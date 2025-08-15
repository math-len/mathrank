package kr.co.mathrank.domain.problem.assessment.exception;

import kr.co.mathrank.domain.problem.assessment.entity.Assessment;

public class AssessmentRegisterException extends AssessmentException {
	public AssessmentRegisterException() {
		super(7001, "등록할 수 있는 권한 부재 (관리자만 등록 가능)");
	}
}
