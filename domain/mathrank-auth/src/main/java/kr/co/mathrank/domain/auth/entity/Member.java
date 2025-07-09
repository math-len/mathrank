package kr.co.mathrank.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import kr.co.mathrank.common.role.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {
	@Id
	private Long id;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(unique = true)
	private String loginId;

	private String password;

	@Embedded
	private final LockInfo lockInfo = new LockInfo();

	public static Member of(Long id, Role role, String loginId, String password) {
		final Member member = new Member();
		member.id = id;
		member.role = role;
		member.loginId = loginId;
		member.password = password;

		return member;
	}
}
