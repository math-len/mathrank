package kr.co.mathrank.domain.auth.client;

record AppleMemberInfoResponse(
	String sub,
	String email
) implements MemberInfoResponse {
	@Override
	public MemberInfo toInfo() {
		return new MemberInfo(sub, null, email, null, null);
	}
}
