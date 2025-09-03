package kr.co.mathrank.client.exception.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 클라이언트 호출 시 발생하는 예외를 AOP로 공통 처리하기 위해
 * 클래스 단위에 부여하는 마커 어노테이션.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Client {
}
