package kr.co.mathrank.app.api.common.authentication;

import org.springframework.web.cors.PreFlightRequestHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.mathrank.common.role.Role;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class AuthorizationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof PreFlightRequestHandler) {
            return true;
        }

        final MemberPrincipal userPrincipal = (MemberPrincipal) request.getUserPrincipal();
        final HandlerMethod handlerMethod = (HandlerMethod) handler;

        final Authorization annotation = handlerMethod.getMethodAnnotation(Authorization.class);
        if (canPassWithoutAuthorization(annotation)) {
            return true;
        }

        if (!isAuthenticated(userPrincipal)) {
            setResponseToUnAuthorized(response);
            return false;
        }

        if (annotation.openedForAll()) {
            return true;
        }

        final Role[] roles = annotation.values();
        if (checkPermission(userPrincipal, roles)) {
            return true;
        }

        log.debug("not authorized");
        setResponseToForbidden(response);
        return false;
    }

    private boolean checkPermission(MemberPrincipal userPrincipal, Role[] roles) {
        if (userPrincipal.hasAnyAuthorization(roles)) {
            log.debug("has permission");
            return true;
        }
        return false;
    }

    private boolean canPassWithoutAuthorization(Authorization annotation) {
        if (annotation == null) {
            log.debug("authorization is not required");
            return true;
        }
        return false;
    }

    private boolean isAuthenticated(MemberPrincipal userPrincipal) {
        if (userPrincipal == null) {
            log.debug("not authorized");
            return false;
        }
        return true;
    }

    private void setResponseToForbidden(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    private void setResponseToUnAuthorized(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
