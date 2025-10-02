package kr.co.mathrank.domain.auth.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;

public record MemberDeleteCommand(
	@NotNull
	Long targetMemberId,
	@NotNull
	Long requestMemberId,
	@NotNull
	Role role
) {
	@AssertTrue(message = "본인 계정만 삭제할 수 있습니다.")
	public boolean isValid() {
		if (targetMemberId == null || requestMemberId == null) {
			return true;
		}

		return requestMemberId.equals(targetMemberId) || role == Role.ADMIN;
	}
}
