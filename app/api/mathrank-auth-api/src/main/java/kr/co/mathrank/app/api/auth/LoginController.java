package kr.co.mathrank.app.api.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Duration;

import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.mathrank.app.api.auth.cookie.RefreshTokenCookieManager;
import kr.co.mathrank.app.api.common.authentication.Authorization;
import kr.co.mathrank.app.api.common.authentication.LoginInfo;
import kr.co.mathrank.app.api.common.authentication.MemberPrincipal;
import kr.co.mathrank.domain.auth.dto.JwtLoginResult;
import kr.co.mathrank.domain.auth.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {
	private static final String ENCODER = "UTF-8";

	private final LoginService loginService;
	private final RefreshTokenCookieManager cookieManager;

	@PostMapping("/api/v1/auth/login")
	public ResponseEntity<LoginResponse> login(
		@RequestBody final Requests.LoginRequest request,
		final HttpServletResponse response
	) {
		final JwtLoginResult result = loginService.login(request.toCommand());
		final ResponseCookie cookie = createRefreshTokenCookie(result.refreshToken(), Duration.ofDays(7));
		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok(new LoginResponse(result.accessToken(), result.userName()));
	}

	@PostMapping("/api/v1/auth/login/refresh")
	@Authorization(openedForAll = true)
	public ResponseEntity<LoginResponse> loginWithRefreshToken(
		@CookieValue(name = RefreshTokenCookieManager.REFRESH_TOKEN_HEADER_NAME) final String refreshToken,
		final HttpServletResponse response
	) {
		final String decodedRefreshToken = decodeToken(refreshToken);

		final JwtLoginResult result = loginService.refresh(decodedRefreshToken);
		final ResponseCookie cookie = createRefreshTokenCookie(result.refreshToken(), Duration.ofDays(7));
		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok(new LoginResponse(result.accessToken(), result.userName()));
	}

	@PostMapping("/api/v1/auth/logout")
	@Authorization(openedForAll = true)
	public ResponseEntity<Void> logout(
		@LoginInfo final MemberPrincipal principal,
		final HttpServletResponse response
	) {
		loginService.logout(principal.memberId());
		final ResponseCookie cookie = createRefreshTokenCookie("", Duration.ZERO);
		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok().build();
	}

	private String decodeToken(final String refreshToken) {
		try {
			return URLDecoder.decode(refreshToken, ENCODER);
		} catch (UnsupportedEncodingException e) {
			log.error("[LoginController.decodeToken] error in decode: {}", refreshToken, e);
			throw new RuntimeException(e);
		}
	}

	private String encodeToken(final String refreshToken) {
		try {
			return URLEncoder.encode(refreshToken, ENCODER);
		} catch (UnsupportedEncodingException e) {
			log.error("[LoginController.encodeToken] error in encode: {}", refreshToken, e);
			throw new RuntimeException(e);
		}
	}

	private ResponseCookie createRefreshTokenCookie(final String refreshToken, final Duration duration) {
		final String encodedToken = encodeToken(refreshToken);
		return this.cookieManager.create(encodedToken, duration);
	}
}
