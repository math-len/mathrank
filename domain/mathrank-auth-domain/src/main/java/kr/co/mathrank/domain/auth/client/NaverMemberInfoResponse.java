package kr.co.mathrank.domain.auth.client;

record NaverMemberInfoResponse(
	NaverResponse response
) implements MemberInfoResponse {
	@Override
	public MemberInfo toInfo() {
		return new MemberInfo(response.id(), response.nickname());
	}

	record NaverResponse(
		String nickname,
		String id
	) {
	}
}
