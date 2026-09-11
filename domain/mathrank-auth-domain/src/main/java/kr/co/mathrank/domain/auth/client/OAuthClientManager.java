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
			.filter(oAuthClientHandler -> oAuthClientHandler.supports(
				command.provider(), command.credentialProfile()))
			.findAny()
			.map(oAuthClientHandler -> oAuthClientHandler.getMemberInfo(command))
			.map(MemberInfoResponse::toInfo)
			.orElseThrow();
	}

	public boolean revoke(final Member member) {
		final List<OAuthClientHandler> matchingHandlers = handlers.stream()
			.filter(oAuthClientHandler -> oAuthClientHandler.supports(member.getOAuthInfo().getOAuthProvider()))
			.toList();
		if (matchingHandlers.isEmpty()) {
			log.info("[OAuthClientManager.revoke] its not a oauth registered user - memberId: {}", member.getId());
			return true;
		}
		return matchingHandlers.stream()
			.anyMatch(handler -> revokeQuietly(handler, member.getOAuthInfo().getOAuthRefreshToken()));
	}

	private boolean revokeQuietly(final OAuthClientHandler handler, final String refreshToken) {
		try {
			return handler.revoke(refreshToken);
		} catch (final RuntimeException exception) {
			log.warn("[OAuthClientManager.revoke] credential profile rejected refresh token - client: {}",
				handler.getClass().getSimpleName());
			return false;
		}
	}

	@PostConstruct
	private void log() {
		log.info("[OAuthClientManager] registered OAuthClients: {}", handlers.stream()
			.map(OAuthClientHandler::getClass)
			.map(Class::getSimpleName)
			.toList());
	}
}
