package kr.co.mathrank.domain.problem.assessment.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AssessmentDurationRangeValidator.class)
public @interface AssessmentDurationConstraint {
	String message() default "시험시간이 잘못 설정됐습니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

	long minIncludeMinutes();
	long maxIncludeMinutes();
}
