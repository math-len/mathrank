package kr.co.mathrank.app.api.common.authentication;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.mathrank.common.jwt.JwtUtil;
import kr.co.mathrank.common.jwt.UserInfo;
import kr.co.mathrank.common.role.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER = "Authorization";
    private final JwtUtil jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(AUTH_HEADER);
        if (authorizationHeader != null) {
            final Role role;
            final Long userId;
            try {
                final UserInfo authInfo = jwtUtils.parse(authorizationHeader);
                role = authInfo.role();
                userId = authInfo.userId();
            } catch (Exception e) {
                log.warn("Error parsing authorization header : {}", e.toString());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            filterChain.doFilter(new JwtRequestWrapper(request, new MemberPrincipal(userId, role)), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
