package kr.co.mathrank.domain.auth.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.jwt.JwtException;
import kr.co.mathrank.common.jwt.JwtResult;
import kr.co.mathrank.common.jwt.JwtUtil;
import kr.co.mathrank.common.jwt.UserInfo;
import kr.co.mathrank.common.role.Role;
import kr.co.mathrank.domain.auth.dto.JwtLoginResult;
import kr.co.mathrank.domain.auth.exception.InvalidRefreshTokenException;
import kr.co.mathrank.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
class JwtLoginManager {
	@Value("${auth.jwt.accessToken.expireTimeMillis}")
	private Long accessTokenExpireTimeMillis;
	@Value("${auth.jwt.refreshToken.expireTimeMillis}")
	private Long refreshTokenExpireTimeMillis;

	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	public JwtLoginResult refresh(@NotNull final String refreshToken) {
		final UserInfo userInfo = getUserInfo(refreshToken);

		// 중복로그인을 방지한다. ( 다른 곳에서 로그인한 사용자가 존재한다. )
		if (!refreshTokenRepository.isValidRefreshToken(userInfo.userId(), refreshToken)) {
			log.warn("[JwtLoginManager.refresh] refresh token is not valid for userId: {}", userInfo.userId());
			throw new InvalidRefreshTokenException();
		}

		return login(userInfo.userId(), userInfo.role(), userInfo.userName());
	}

	public JwtLoginResult login(@NotNull final Long userId, @NotNull final Role role, @NotNull final String userName) {
		final JwtResult result = jwtUtil.createJwt(userId, role, userName, accessTokenExpireTimeMillis, refreshTokenExpireTimeMillis);
		refreshTokenRepository.refresh(userId, result.refreshToken(), Duration.ofMillis(refreshTokenExpireTimeMillis));

		return JwtLoginResult.from(result, userName);
	}

	public void logout(@NotNull final String refreshToken) {
		final UserInfo userInfo = jwtUtil.parse(refreshToken);
		refreshTokenRepository.expire(userInfo.userId());
	}

	private UserInfo getUserInfo(final String refreshToken) {
		try {
			return jwtUtil.parse(refreshToken);
		} catch (JwtException e) {
			log.warn("[JwtLoginManager.getUserInfo] refresh token is invalid: {}, error message:{}", refreshToken, e.getMessage(), e);
			throw new InvalidRefreshTokenException();
		}
	}
}
