package kr.co.mathrank.app.api.common.authentication;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.mathrank.common.jwt.JwtException;
import kr.co.mathrank.common.jwt.JwtUtil;
import kr.co.mathrank.common.jwt.UserInfo;
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

        // 헤더가 존재하면 인증됐는지 검사한다.
        if (authorizationHeader != null) {
            UserInfo userInfo;
            try {
                userInfo = jwtUtils.parse(authorizationHeader);
            } catch (JwtException e) {
                log.warn("[JwtAuthenticationFilter.doFilterInternal] error occurred in parsing access token : {}", e.toString(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            filterChain.doFilter(new JwtRequestWrapper(request, new MemberPrincipal(userInfo.userId(), userInfo.role())), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
