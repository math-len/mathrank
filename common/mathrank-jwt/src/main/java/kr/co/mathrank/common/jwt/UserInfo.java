package kr.co.mathrank.common.jwt;

import kr.co.mathrank.common.role.Role;

public record UserInfo(
	Long userId,
	Role role,
	String userName
) {
}
