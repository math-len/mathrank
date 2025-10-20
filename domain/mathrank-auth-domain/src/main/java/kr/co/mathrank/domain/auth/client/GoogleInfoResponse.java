package kr.co.mathrank.domain.auth.client;

public record GoogleInfoResponse(
	String id,
	String given_name
) implements MemberInfoResponse{
	@Override
	public MemberInfo toInfo() {
		return new MemberInfo(id(), given_name());
	}
}
