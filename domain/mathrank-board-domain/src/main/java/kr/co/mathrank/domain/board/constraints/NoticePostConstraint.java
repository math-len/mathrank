package kr.co.mathrank.domain.board.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoticePostConstraintValidator.class)
public @interface NoticePostConstraint {
	String message() default "공지사항은 관리자만 작성 가능합니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
