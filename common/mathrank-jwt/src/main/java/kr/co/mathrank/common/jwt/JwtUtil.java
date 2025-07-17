package kr.co.mathrank.common.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import kr.co.mathrank.common.role.Role;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Component
public class JwtUtil {
	private static final String TOKEN_TYPE = "Bearer";
	private static final String TOKEN_FORMAT = TOKEN_TYPE + " " + "%s";

	private static final String ROLE_CLAIM_NAME = "memberRole";
	private static final String USERNAME_CLAIM_NAME = "memberName";

	@Value("${server.jwt.algorithm}")
	private String encryptAlgorithm;
	@Value("${server.jwt.key}")
	private String jwtKey;
	private SignatureAlgorithm signatureAlgorithm;
	private SecretKey secretKey;
	private JwtParser jwtParser;

	@PostConstruct
	private void initJwt() {
		this.signatureAlgorithm = SignatureAlgorithm.forName(this.encryptAlgorithm);
		final byte[] decodedBytes = Base64.getDecoder().decode(this.jwtKey.getBytes());
		this.secretKey = Keys.hmacShaKeyFor(decodedBytes);
		this.jwtParser = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build();
	}

	public JwtResult createJwt(@NotNull final Long memberId, @NotNull final Role role, @NotNull final String userName,
		@NotNull final Long accessTokenExpirationTimeMillis, @NotNull final Long refreshTokenExpirationTimeMillis) {
		final Date now = new Date();

		final String accessToken = generateToken(memberId, userName, role, accessTokenExpirationTimeMillis, now);
		final String refreshToken = generateToken(memberId, userName, role, refreshTokenExpirationTimeMillis, now);
		return new JwtResult(accessToken, refreshToken);
	}

	public UserInfo parse(@NotNull final String token) throws JwtException {
		try {
			final Claims claims = this.parseToken(token);
			return new UserInfo(
				getMemberId(claims).orElseThrow(() -> new IllegalArgumentException("cannot find member id from token")),
				getMemberRole(claims).orElseThrow(() -> new IllegalArgumentException("cannot find member role from token")),
				getMemberName(claims).orElseThrow(() -> new IllegalArgumentException("cannot find member name from token"))
			);
		} catch (Exception e) {
			throw new JwtException(e);
		}
	}

	private String generateToken(final Long memberId, final String userName, final Role role, final long expirationTime,
		final Date now) {
		return TOKEN_FORMAT.formatted(Jwts.builder()
			.setSubject(String.valueOf(memberId))
			.claim(ROLE_CLAIM_NAME, role)
			.claim(USERNAME_CLAIM_NAME, userName)
			.setId(UUID.randomUUID().toString())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + expirationTime))
			.signWith(secretKey, signatureAlgorithm)
			.compact());
	}

	private Claims parseToken(final String jwtHeaderValue) {
		final String token = jwtHeaderValue.replace(TOKEN_TYPE, "").trim();
		return jwtParser.parseClaimsJws(token).getBody();
	}

	private Optional<Long> getMemberId(final Claims claims) {
		final String subject = claims.getSubject();
		final Long memberId = Long.parseLong(subject);
		return Optional.of(memberId);
	}

	private Optional<Role> getMemberRole(final Claims claims) {
		final String role = claims.get(ROLE_CLAIM_NAME, String.class);
		if (role == null) {
			return Optional.empty();
		}
		return Optional.of(Role.valueOf(role));
	}

	protected Optional<String> getMemberName(final Claims claims) {
		final String name = claims.get(USERNAME_CLAIM_NAME, String.class);
		return Optional.ofNullable(name);
	}
}
