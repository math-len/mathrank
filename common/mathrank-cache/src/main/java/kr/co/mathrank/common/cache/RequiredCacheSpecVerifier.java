package kr.co.mathrank.common.cache;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link RequiredCacheSpec} 들을 모아 캐시 이름의 중복 여부를 검증하는 컴포넌트.
 *
 * <p>동작 시점:
 * <ul>
 *   <li>{@link ApplicationReadyEvent} 발생 시점(애플리케이션 기동 완료 직후)에 실행된다.</li>
 * </ul>
 *
 * <p>검증 내용:
 * <ul>
 *   <li>모든 {@link RequiredCacheSpec} 의 {@code cacheName()} 값을 수집한다.</li>
 *   <li>이미 등장한 캐시 이름이 또 나오면 에러 로그를 남기고 {@link IllegalStateException} 을 발생시켜
 *       애플리케이션 기동을 중단한다 (fail-fast).</li>
 *   <li>중복이 없으면 아무 동작 없이 정상적으로 통과한다.</li>
 * </ul>
 *
 * <p>주요 목적:
 * <ul>
 *   <li>멀티 모듈 환경에서 서로 다른 모듈이 동일한 캐시 이름을 선언하는 문제를 사전에 차단</li>
 *   <li>운영 중 캐시 이름 충돌로 인해 TTL/설정이 꼬이는 문제를 예방</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
class RequiredCacheSpecVerifier {
	private final List<RequiredCacheSpec> requiredCacheSpecs;

	@EventListener(ApplicationReadyEvent.class)
	private void verifyCacheNamesDuplicated() {
		final Set<String> cacheNames = new HashSet<>();

		for (final RequiredCacheSpec requiredCacheSpec : requiredCacheSpecs) {
			final String cacheName = requiredCacheSpec.cacheName();

			if (cacheNames.contains(cacheName)) {
				log.error("[RequiredCacheSpecVerifier.verifyCacheNamesDuplicated] cache name duplicated : {}", cacheName);
				throw new IllegalStateException("cache name duplicated");
			} else {
				cacheNames.add(requiredCacheSpec.cacheName());
			}
		}
	}
}
