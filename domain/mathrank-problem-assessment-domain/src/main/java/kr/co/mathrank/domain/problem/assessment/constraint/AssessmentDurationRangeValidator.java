package kr.co.mathrank.domain.problem.assessment.constraint;

import java.time.Duration;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class AssessmentDurationRangeValidator implements ConstraintValidator<AssessmentDurationConstraint, Duration> {
	private long minIncludeMinutes;
	private long maxIncludeMinutes;

	@Override
	public void initialize(AssessmentDurationConstraint assessmentDurationConstraint) {
		this.minIncludeMinutes = assessmentDurationConstraint.minIncludeMinutes();
		this.maxIncludeMinutes = assessmentDurationConstraint.maxIncludeMinutes();
	}

	@Override
	public boolean isValid(Duration value, ConstraintValidatorContext context) {
		if (value == null) {
			return true; // @NotNull은 별도 처리
		}
		long minutes = value.toMinutes();
		return minutes >= minIncludeMinutes && minutes <= maxIncludeMinutes;
	}
}
