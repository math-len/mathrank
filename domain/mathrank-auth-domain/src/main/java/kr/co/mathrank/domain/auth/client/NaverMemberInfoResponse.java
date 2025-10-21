package kr.co.mathrank.domain.auth.client;

record NaverMemberInfoResponse(
	NaverResponse response
) implements MemberInfoResponse {
	@Override
	public MemberInfo toInfo() {
		return new MemberInfo(response.id(), response.nickname(), response.email(), null, null);
	}

	record NaverResponse(
		String nickname,
		String id,
		String email
	) {
	}
}
