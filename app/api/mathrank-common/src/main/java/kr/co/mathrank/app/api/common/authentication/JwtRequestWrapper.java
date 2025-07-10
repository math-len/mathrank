package kr.co.mathrank.app.api.common.authentication;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

class JwtRequestWrapper extends HttpServletRequestWrapper {
    private final MemberPrincipal userPrincipal;
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public JwtRequestWrapper(HttpServletRequest request, MemberPrincipal userPrincipal) {
        super(request);
        this.userPrincipal = userPrincipal;
    }

    @Override
    public Principal getUserPrincipal() {
        return userPrincipal;
    }
}
