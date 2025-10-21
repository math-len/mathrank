package kr.co.mathrank.domain.auth.client;

public record MemberInfoRefreshTokenAdapter(MemberInfo memberInfo, String refreshToken, String tokenType)
	implements MemberInfoResponse {
	@Override
	public MemberInfo toInfo() {
		return new MemberInfo(
			memberInfo.memberId(),
			memberInfo.nickName(),
			memberInfo.email(),
			refreshToken,
			tokenType
		);
	}
}
