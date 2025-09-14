package kr.co.mathrank.rank.read;

import kr.co.mathrank.client.internal.member.MemberInfo;
import kr.co.mathrank.domain.rank.dto.RankItemResult;
import kr.co.mathrank.domain.rank.entity.Tier;

public record RankPageItemResponse(
	MemberResponse memberInfo,
	Long rank,
	Tier tier,
	Long score,
	Long totalSubmittedCount,
	Long successCount
) {
	public static RankPageItemResponse from(final RankItemResult result, final MemberInfo memberInfo) {
		return new RankPageItemResponse(
			MemberResponse.from(memberInfo),
			result.rank(),
			result.tier(),
			result.score(),
			result.totalSubmittedCount(),
			result.successCount()
		);
	}

	record MemberResponse(
		String memberId,
		String nickName
	) {
		static MemberResponse from(MemberInfo memberInfo) {
			return new MemberResponse(String.valueOf(memberInfo.memberId()), memberInfo.memberName());
		}
	}
}
