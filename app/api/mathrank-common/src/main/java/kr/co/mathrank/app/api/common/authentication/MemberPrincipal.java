package kr.co.mathrank.app.api.common.authentication;

import java.security.Principal;
import java.util.Arrays;

import kr.co.mathrank.common.role.Role;

public class MemberPrincipal implements Principal {
    private final Long memberId;
    private final Role role;

    public MemberPrincipal(final Long userId, final Role role) {
        this.memberId = userId;
        this.role = role;
    }

    public Long memberId() {
        return memberId;
    }

    public Role role() {
        return role;
    }

    @Override
    public String getName() {
        return "";
    }

    public boolean hasAnyAuthorization(final Role... roles) {
        return Arrays.stream(roles).anyMatch(role -> this.role == role);
    }
}
