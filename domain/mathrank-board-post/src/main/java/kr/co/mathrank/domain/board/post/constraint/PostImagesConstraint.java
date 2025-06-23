package kr.co.mathrank.domain.board.post.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {})
@ReportAsSingleViolation

@NotNull
@Size(max = 5)
public @interface PostImagesConstraint {
	String message() default "게시글 이미지 갯수 제한";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
