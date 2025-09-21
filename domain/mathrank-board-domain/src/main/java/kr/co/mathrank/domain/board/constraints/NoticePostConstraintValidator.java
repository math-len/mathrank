package kr.co.mathrank.domain.board.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.mathrank.common.role.Role;

class NoticePostConstraintValidator implements ConstraintValidator<NoticePostConstraint, Role> {
	@Override
	public boolean isValid(Role role, ConstraintValidatorContext constraintValidatorContext) {
		if (role == null) {
			return false;
		}

		return role == Role.ADMIN;
	}
}
