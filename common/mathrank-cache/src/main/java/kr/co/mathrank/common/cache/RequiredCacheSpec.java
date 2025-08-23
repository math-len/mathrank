package kr.co.mathrank.common.cache;

import java.time.Duration;

/**
 * 각 모듈에서 필수적으로 정의해야 하는 캐시 사양(스펙) 인터페이스입니다.
 * <p>
 * 이 인터페이스를 구현하여 모듈 단위로 캐시 이름, TTL 등
 * 캐시 설정 정보를 중앙 구성(예: CacheManager)으로 전달할 수 있습니다.
 *
 * <ul>
 *   <li>{@link #moduleName()} - 해당 캐시를 정의한 모듈명 (로깅/추적용)</li>
 *   <li>{@link #cacheName()} - 캐시 식별자. CacheManager에 등록될 캐시 이름</li>
 *   <li>{@link #ttl()} - Time-To-Live (만료 시간). 캐시 항목의 유효 기간</li>
 * </ul>
 *
 * 구현체는 보통 @Configuration 클래스나 @Bean 으로 제공되어,
 * 전역 CacheManager 초기화 시 수집되어 적용됩니다.
 */
public interface RequiredCacheSpec {
	public abstract String moduleName();
	public abstract String cacheName();
	public abstract Duration ttl();
}
