package kr.co.mathrank.domain.auth.client;

public record MemberInfo(
	String memberId,
	String nickName,
	String email,
	String refreshToken,
	String tokenType
) {
}
