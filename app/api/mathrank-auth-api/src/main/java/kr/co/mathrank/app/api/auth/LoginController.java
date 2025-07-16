package kr.co.mathrank.app.api.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.mathrank.app.api.auth.cookie.RefreshTokenCookieManager;
import kr.co.mathrank.domain.auth.dto.JwtLoginResult;
import kr.co.mathrank.domain.auth.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "로그인/로그아웃 API", description = "JWT를 통해 구현되며, accessToken을 통해 인증 인가를 수행합니다.")
@RestController
@RequiredArgsConstructor
public class LoginController {
	private static final String ENCODER = "UTF-8";

	private final LoginService loginService;
	private final RefreshTokenCookieManager cookieManager;

	@Operation(summary = "로그인 API", description = "로그인 성공 시, accessToken을 body로 refreshToken을 쿠키로 응답합니다. 중복 로그인 시, 이전에 발급된 refreshToken이 무효화 처리 됩니다.")
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

	@Operation(summary = "로그인 갱신 API", description = "로그인 시 응답받은 refreshToken을 통해 accessToken을 새로 갱신받습니다. 무효한 refreshToken 사용시 에러를 응답합니다.")
	@PostMapping("/api/v1/auth/login/refresh")
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

	@Operation(summary = "로그아웃 API", description = "클라이언트의 refreshToken을 무효화합니다.")
	@PostMapping("/api/v1/auth/logout")
	public ResponseEntity<Void> logout(
		@CookieValue(name = RefreshTokenCookieManager.REFRESH_TOKEN_HEADER_NAME) final String refreshToken,
		final HttpServletResponse response
	) {
		final String decodedRefreshToken = decodeToken(refreshToken);
		loginService.logout(decodedRefreshToken);
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
