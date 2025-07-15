package kr.co.mathrank.app.api.auth.cookie;

import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;

class DeployCookieManager extends RefreshTokenCookieManager {
	@Override
	protected void customize(ResponseCookie.ResponseCookieBuilder builder) {
		builder.sameSite(Cookie.SameSite.STRICT.name());
	}
}
