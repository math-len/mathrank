package kr.co.mathrank.app.api.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Duration;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.mathrank.app.api.auth.cookie.RefreshTokenCookieManager;
import kr.co.mathrank.domain.auth.dto.JwtLoginResult;
import kr.co.mathrank.domain.auth.entity.OAuthProvider;
import kr.co.mathrank.domain.auth.service.LoginService;
import kr.co.mathrank.domain.auth.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "로그인/로그아웃 API", description = "JWT를 통해 구현되며, accessToken을 통해 인증 인가를 수행합니다.")
@RestController
@RequiredArgsConstructor
public class LoginController {
	private static final String ENCODER = "UTF-8";

	private final LoginService loginService;
	private final OAuthLoginService oAuthLoginService;
	private final RefreshTokenCookieManager cookieManager;

	@Operation(summary = "oAuth 로그인 API", description = """
        oAuth를 통한 로그인 및 회원가입 API입니다.
        처음 등록되는 OAuth 계정인 경우 자동으로 회원가입이 진행됩니다.

        [주의] `redirectUri`는 OAuth 제공자(Kakao, Naver 등) 개발자 콘솔에 사전 등록된 URI만 사용 가능합니다.
        프론트엔드 개발자는 사용할 redirectUri를 사전에 백엔드 개발자에게 전달해야 하며,
        URI를 미리 등록하지 않고 사용할 경우 OAuth 인증이 실패합니다.

        예시: https://yourapp.com/oauth/callback/kakao
    """)
	@GetMapping("/api/v1/auth/login/oauth/{provider}")
	public ResponseEntity<LoginResponse> loginByoAuth(
		@PathVariable final OAuthProvider provider,
		@ParameterObject @ModelAttribute final Requests.OAuthLoginRequest loginRequest,
		final HttpServletResponse response
	) {
		log.debug("[LoginOAuthController.redirectFromOAuth] redirect from OAuth - provider: {}, info: {}", provider, loginRequest);

		final JwtLoginResult result = oAuthLoginService.login(loginRequest.toCommand(provider));
		final ResponseCookie cookie = createRefreshTokenCookie(result.refreshToken(), Duration.ofDays(7));
		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok(new LoginResponse(result.accessToken(), result.userName()));
	}

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
