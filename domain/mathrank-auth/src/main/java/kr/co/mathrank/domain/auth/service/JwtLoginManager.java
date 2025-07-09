package kr.co.mathrank.domain.auth.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.jwt.JwtResult;
import kr.co.mathrank.common.jwt.JwtUtil;
import kr.co.mathrank.common.jwt.UserInfo;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.dto.JwtLoginResult;
import kr.co.mathrank.domain.auth.exception.AuthException;
import kr.co.mathrank.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class JwtLoginManager {
	@Value("${auth.jwt.accessToken.expireTimeMillis}")
	private Long accessTokenExpireTimeMillis;
	@Value("${auth.jwt.refreshToken.expireTimeMillis}")
	private Long refreshTokenExpireTimeMillis;

	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	public JwtLoginResult refresh(@NotNull final String refreshToken) {
		final UserInfo userInfo = jwtUtil.parse(refreshToken);
		if (refreshTokenRepository.isValidRefreshToken(userInfo.userId(), refreshToken)) {
			return login(userInfo.userId(), Role.USER);
		}
		log.warn("[JwtLoginManager.refresh] refresh token is not valid for userId: {}", userInfo.userId());
		throw new AuthException();
	}

	JwtLoginResult login(@NotNull final Long userId, @NotNull final Role role) {
		final JwtResult result = jwtUtil.createJwt(userId, role, accessTokenExpireTimeMillis, refreshTokenExpireTimeMillis);
		refreshTokenRepository.refresh(userId, result.refreshToken(), Duration.ofMillis(refreshTokenExpireTimeMillis));

		return JwtLoginResult.from(result);
	}
}
