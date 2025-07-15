package kr.co.mathrank.app.api.auth.cookie;

import java.time.Duration;

import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;

public abstract class RefreshTokenCookieManager {
	public static final String REFRESH_TOKEN_HEADER_NAME = "refreshToken";

	public ResponseCookie create(final String encodedCookieValue, final Duration maxAge) {
		final ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_TOKEN_HEADER_NAME, encodedCookieValue)
			.httpOnly(true)
			.secure(true)
			.maxAge(maxAge)
			.sameSite(Cookie.SameSite.STRICT.name())
			.path("/api/v1/auth");

		this.customize(builder);

		return builder.build();
	}

	protected abstract void customize(final ResponseCookie.ResponseCookieBuilder builder);
}
