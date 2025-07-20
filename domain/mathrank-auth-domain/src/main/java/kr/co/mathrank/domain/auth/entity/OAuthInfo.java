package kr.co.mathrank.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class OAuthInfo {
	@Enumerated(EnumType.STRING)
	@Column(name = "oauth_provider")
	private OAuthProvider oAuthProvider;

	@Column(name = "oauth_user_id")
	private Long oAuthUserId;
}
