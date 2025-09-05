package kr.co.mathrank.app.api.member;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.dto.MemberUpdateCommand;
import kr.co.mathrank.domain.auth.entity.MemberType;

public class Requests {
	public record MemberUpdateRequest(
		@NotNull String userNickName,
		@NotNull MemberType memberType,
		@NotNull String schoolCode,
		@NotNull Boolean agreeToPolicy
	) {
		MemberUpdateCommand toCommand(final Long memberId) {
			return new MemberUpdateCommand(
				memberId,
				userNickName,
				memberType,
				schoolCode,
				agreeToPolicy
			);
		}
	}
}
