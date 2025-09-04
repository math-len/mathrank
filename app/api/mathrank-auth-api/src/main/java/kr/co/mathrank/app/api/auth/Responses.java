package kr.co.mathrank.app.api.auth;

import kr.co.mathrank.domain.auth.dto.MemberInfoResult;

public class Responses {
	public static record LoginOAuthResponse(
		String accessToken,
		String userName,
		Boolean isNewUser
	) {
	}

	public static record LoginResponse(
		String accessToken,
		String userName
	) {
	}

	public record MemberInfoResponse(
		Long memberId,
		String memberName
	) {
		public static MemberInfoResponse from(final MemberInfoResult result) {
			return new MemberInfoResponse(result.memberId(), result.nickName());
		}
	}
}
