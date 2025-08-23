package kr.co.mathrank.common.cache;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 애플리케이션 구동 완료 시점에 {@link CacheSpecAssembler} 빈들의 등록 여부를 검증하고 로그를 남기는 컴포넌트.
 *
 * <p>주요 목적:
 * <ul>
 *   <li>멀티모듈 환경에서 모듈별 캐시 스펙 조립기가 정상적으로 등록됐는지 초기 구동 단계에서 점검</li>
 *   <li>운영 중 캐시 스펙 조립기 누락 문제를 조기 발견 후 로깅</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
class CacheSpecAssemblerVerifier {
	private final List<CacheSpecAssembler> cacheSpecAssemblers;

	@EventListener(ApplicationReadyEvent.class)
	private void verifyAssemblers() {
		if (cacheSpecAssemblers.isEmpty()) {
			log.warn("[CacheSpecAssemblerVerifier.verifyAssemblers] cannot find any cache assemblers");
			return;
		}
		log.info("[CacheSpecAssemblerVerifier.verifyAssemblers] cache assemblers detected - count: {}", cacheSpecAssemblers.size());
	}
}
