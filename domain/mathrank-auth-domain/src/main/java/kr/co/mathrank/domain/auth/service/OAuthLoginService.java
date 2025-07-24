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
import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.Member;
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

	private JwtLoginResult jwtLogin(final Member member) {
		return jwtLoginManager.login(member.getId(), member.getRole(), member.getName());
	}
}
