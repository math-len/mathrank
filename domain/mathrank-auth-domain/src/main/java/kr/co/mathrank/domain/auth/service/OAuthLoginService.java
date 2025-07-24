package kr.co.mathrank.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.common.snowflake.Snowflake;
import kr.co.mathrank.domain.auth.client.MemberInfo;
import kr.co.mathrank.domain.auth.client.OAuthClientManager;
import kr.co.mathrank.domain.auth.dto.JwtLoginResult;
import kr.co.mathrank.domain.auth.dto.JwtOAuthLoginResult;
import kr.co.mathrank.domain.auth.dto.MemberInfoCompleteCommand;
import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.Member;
import kr.co.mathrank.domain.auth.exception.CannotFoundMemberException;
import kr.co.mathrank.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class OAuthLoginService {
	private final OAuthClientManager oAuthClient;
	private final JwtLoginManager jwtLoginManager;
	private final MemberRepository memberRepository;
	private final Snowflake snowflake;

	@Transactional
	public JwtOAuthLoginResult login(@NotNull @Valid final OAuthLoginCommand command) {
		final MemberInfo memberInfo = oAuthClient.getMemberInfo(command);

		final Member member = memberRepository.findByOAuthIdAndProvider(memberInfo.memberId(), command.provider()).stream()
			// 사용자 존재 시, 로그인
			.findAny()
			// 사용자 없을 시, 회원가입
			.orElseGet(() -> {
				final Long uniqueId = snowflake.nextId();

				final Member newMember = Member.fromOAuth(uniqueId, memberInfo.memberId(), command.provider(),
					memberInfo.nickName(), Role.USER);
				memberRepository.save(newMember);
				log.info("[OAuthLoginService.login] member registered - memberId: {}, oAuthProvider: {}", uniqueId, command.provider());
				return newMember;
			});

		log.info("[OAuthLoginService.login] member login success with oAuth - memberId: {}, oAuthProvider: {}", member.getId(), member.getOAuthInfo().getOAuthProvider());
		// 사용자의 등록 지연 여부를 함께 응답한다.
		return JwtOAuthLoginResult.from(jwtLogin(member), member.getPending());
	}

	/**
	 * 필수정보가 누락된 oauth 기반 등록 계정을 유효하도록 업데이트 하는 api입니다.
	 *
	 * @param command
	 */
	@Transactional
	public void completeRegister(@NotNull @Valid final MemberInfoCompleteCommand command) {
		memberRepository.findById(command.memberId())
			.ifPresentOrElse(member -> {
					// 사용자 존재 시, 업데이트 마무리
					member.completeRegister(command.memberType(), command.agreeToPrivacyPolicy(), command.schoolCodes());
				},
				() -> {
					// 사용자를 찾을 수 없을 때, 예외처리
					log.warn("[MemberRegisterService.completeRegister] cannot find member: {}", command.memberId());
					throw new CannotFoundMemberException();
				});
		log.info("[MemberRegisterService.completeRegister] member register completed - memberId: {}", command.memberId());
	}

	private JwtLoginResult jwtLogin(final Member member) {
		return jwtLoginManager.login(member.getId(), member.getRole(), member.getName());
	}
}
