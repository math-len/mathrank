package kr.co.mathrank.domain.board.post.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotEmpty;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {})
@ReportAsSingleViolation

@NotEmpty
public @interface PostTitleConstraint {
	String message() default "게시글 제목 제한";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
