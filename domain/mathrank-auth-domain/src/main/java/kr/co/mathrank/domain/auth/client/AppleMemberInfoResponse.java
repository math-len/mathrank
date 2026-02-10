package kr.co.mathrank.domain.auth.client;

record AppleMemberInfoResponse(
	String sub,
	String email
) implements MemberInfoResponse {
	@Override
	public MemberInfo toInfo() {
		return new MemberInfo(sub, getNickName(), email, null, null);
	}

	private String getNickName() {
		if (email != null && email.contains("@")) {
			return email.substring(0, email.indexOf("@"));
		}
		return sub;
	}
}
