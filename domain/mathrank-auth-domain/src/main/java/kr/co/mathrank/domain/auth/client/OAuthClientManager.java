package kr.co.mathrank.domain.auth.client;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.domain.auth.dto.OAuthLoginCommand;
import kr.co.mathrank.domain.auth.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class OAuthClientManager {
	private final List<OAuthClientHandler> handlers;

	public MemberInfo getMemberInfo(@NotNull @Valid final OAuthLoginCommand command) {
		return handlers.stream()
			.filter(oAuthClientHandler -> oAuthClientHandler.supports(command.provider()))
			.findAny()
			.map(oAuthClientHandler -> oAuthClientHandler.getMemberInfo(command))
			.map(MemberInfoResponse::toInfo)
			.orElseThrow();
	}

	public boolean revoke(final Member member) {
		return handlers.stream()
			.filter(oAuthClientHandler -> oAuthClientHandler.supports(member.getOAuthInfo().getOAuthProvider()))
			.findAny()
			.map(oAuthClientHandler -> oAuthClientHandler.revoke(member.getOAuthInfo().getOAuthRefreshToken()))
			.orElseGet(() -> {
				log.info("[OAuthClientManager.revoke] its not a oauth registered user - memberId: {}", member.getId());
				return true;
			});
	}

	@PostConstruct
	private void log() {
		log.info("[OAuthClientManager] registered OAuthClients: {}", handlers.stream()
			.map(OAuthClientHandler::getClass)
			.map(Class::getSimpleName)
			.toList());
	}
}
