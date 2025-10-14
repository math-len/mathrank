package kr.co.mathrank.app.api.member.read;

import java.time.LocalDateTime;

import kr.co.mathrank.client.external.school.SchoolInfo;
import kr.co.mathrank.client.internal.point.PointInfo;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.dto.MemberInfoResult;
import kr.co.mathrank.domain.auth.entity.MemberType;

public class Responses {
	record MemberInfoDetailResponse(
		String memberId,
		String nickName,
		Role role,
		MemberType memberType,
		LocalDateTime createdAt,
		Boolean agreeToPrivacyPolicy,
		Boolean pending,
		SchoolResponse schoolDetail,
		Long remainPoint
	) {
		public static MemberInfoDetailResponse from(final MemberInfoResult result, final SchoolInfo schoolInfo, final
			PointInfo pointInfo) {
			return new MemberInfoDetailResponse(
				String.valueOf(result.memberId()),
				result.nickName(),
				result.role(),
				result.memberType(),
				result.createdAt(),
				result.agreeToPrivacyPolicy(),
				result.pending(),
				SchoolResponse.from(schoolInfo),
				pointInfo.remainPoint()
			);
		}
	}

	public record SchoolResponse(
		String schoolName,
		String schoolCode,
		String schoolKind,
		String schoolLocation
	) {
		public static SchoolResponse from(final SchoolInfo schoolInfo) {
			return new SchoolResponse(
				schoolInfo.SCHUL_NM(),
				schoolInfo.SD_SCHUL_CODE(),
				schoolInfo.SCHUL_KND_SC_NM(),
				schoolInfo.ORG_RDNMA()
			);
		}
	}
}
