package kr.co.mathrank.app.api.common.authentication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import kr.co.mathrank.common.role.Role;

/**
 * 인증이 필요한 API에 적용하는 어노테이션 입니다.
 *
 * 어노테이션 적용 시, 해당 API를 사용하기 위해선 인증이 필요합니다.
 *
 * 미 적용시, 인증되지 않은 사용자도 해당 API를 사용 가능합니다.
 * {@link AuthorizationInterceptor} 를 통해 권한을 검사합니다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Authorization {
    /**
     * 접근 가능한 권한을 명시해주세요
     * {@link Role}
     * @return 접근 가능한 권한을 리턴합니다.
     */
    Role[] values() default {};

    /**
     * {@code true} 인증만 됐다면 해당 API를 사용가능하도록 합니다. ( {@code values()} 무시 )
     * {@code false} {@code values()}를 통해 접근 가능한 권한을 확인합니다.
     * @return
     */
    boolean openedForAll() default false;
}
