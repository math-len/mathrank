package kr.co.mathrank.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.co.mathrank.common.role.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member", indexes = {
	@Index(name = "idx_oauthUserId", columnList = "oauth_user_id")
})
@Getter
public class Member {
	@Id
	private Long id;

	private String name;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(unique = true)
	private String loginId;

	private String password;

	@Embedded
	private final OAuthInfo oAuthInfo = new OAuthInfo();

	@Embedded
	private final LockInfo lockInfo = new LockInfo();

	public static Member of(Long id, String name, Role role, String loginId, String password) {
		final Member member = new Member();
		member.id = id;
		member.role = role;
		member.name = name;
		member.loginId = loginId;
		member.password = password;

		return member;
	}

	public static Member fromOAuth(final Long id, final Long oAuthId, final OAuthProvider provider,
		final String nickName, final Role role) {
		final Member member = new Member();
		member.id = id;
		member.oAuthInfo.setOAuthUserId(oAuthId);
		member.oAuthInfo.setOAuthProvider(provider);
		member.name = nickName;
		member.role = role;

		return member;
	}
}
