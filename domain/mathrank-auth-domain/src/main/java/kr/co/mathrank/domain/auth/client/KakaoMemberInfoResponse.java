package kr.co.mathrank.domain.auth.client;

import kr.co.mathrank.domain.auth.exception.InvalidOAuthLoginException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
record KakaoMemberInfoResponse(
	// oauth 에 저장된 회원의 고유 식별 번호
	Long id,
	Account kakao_account
) implements MemberInfoResponse {

	public String getNickName() {
		try {
			return kakao_account.profile.nickname;
		} catch (Exception e) {
			log.error("[KakaoMemberInfoResponse.getNickName] cannot parse nickName: {}", this, e);
			throw new InvalidOAuthLoginException("카카오 서버로부터 사용할 수 없는 메시지를 받았습니다.");
		}
	}

	@Override
	public MemberInfo toInfo() {
		return new MemberInfo(id, getNickName());
	}

	record Account (
		Profile profile
	) {

	}
	record Profile(
		String nickname
	) {
	}
}
