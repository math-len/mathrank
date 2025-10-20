package kr.co.mathrank.domain.auth.client;

public record MemberInfo(
	String memberId,
	String nickName,
	String refreshToken,
	String tokenType
) {
}
