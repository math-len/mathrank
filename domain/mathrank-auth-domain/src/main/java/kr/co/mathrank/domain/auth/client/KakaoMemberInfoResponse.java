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
		return new MemberInfo(String.valueOf(id), getNickName(), kakao_account().getEmail(), null, null);
	}

	record Account (
		Profile profile,
		Boolean is_email_valid, // 유효한 이메일인지 ( 마스킹 되어 있는지 )
		Boolean is_email_verified, // 인증된 이메일인지 ( 이메일 인증 완료 됐는지 )
		String email
	) {
		public String getEmail() {
			if (is_email_valid == null || is_email_verified == null) {
				return null;
			}

			if (!is_email_valid || !is_email_verified) {
				log.info("[KakaoMemberInfoResponse.getEmail] email is not available - email: {}", this);
				return null;
			}

			return email;
		}
	}

	record Profile(
		String nickname
	) {
	}
}
