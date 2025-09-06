package kr.co.mathrank.domain.auth.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kr.co.mathrank.common.role.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member", indexes = {
	@Index(name = "idx_oAuthUserId_oAuthProvider", columnList = "oauth_user_id, oauth_provider")
})
@Getter
public class Member {
	@Id
	private Long id;

	@Setter
	private String name;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Enumerated(EnumType.STRING)
	@Setter
	private MemberType memberType;

	@Column(unique = true)
	private String loginId;

	private String password;

	@Embedded
	private final OAuthInfo oAuthInfo = new OAuthInfo();

	@Embedded
	private final LockInfo lockInfo = new LockInfo();

	@CreationTimestamp
	private LocalDateTime createdAt;

	@Setter
	private Boolean agreeToPrivacyPolicy = false;

	@Setter
	private Boolean pending = true;

	@Setter
	private String schoolCode;

	public static Member of(final Long id, final String name, final Role role, final String loginId,
		final String password, final MemberType memberType, final Boolean agreeToPrivacyPolicy,
		final String schoolCode) {
		final Member member = new Member();
		member.id = id;
		member.role = role;
		member.name = name;
		member.loginId = loginId;
		member.password = password;
		member.completeRegister(memberType, agreeToPrivacyPolicy, schoolCode, name);

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

	public void completeRegister(final MemberType memberType, final Boolean agreeToPrivacyPolicy, final String schoolCode, final String nickName) {
		this.setMemberType(memberType);
		this.setName(nickName);
		this.setAgreeToPrivacyPolicy(agreeToPrivacyPolicy);
		this.setSchoolCode(schoolCode);
		this.setPending(false);
	}
}
